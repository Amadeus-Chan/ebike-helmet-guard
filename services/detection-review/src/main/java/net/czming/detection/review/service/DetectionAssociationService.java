package net.czming.detection.review.service;

import cn.smartjavaai.common.entity.DetectionInfo;
import cn.smartjavaai.common.entity.DetectionRectangle;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DetectionAssociationService {

    private static final double RIDER_EXPAND_RATIO_X = 0.08;
    private static final double RIDER_EXPAND_RATIO_Y = 0.08;

    private static final double HELMET_MAX_CENTER_Y_RATIO = 0.55;
    private static final double PLATE_MIN_CENTER_Y_RATIO = 0.50;

    private static final double HELMET_MIN_OVERLAP_RATIO = 0.50;
    private static final double PLATE_MIN_OVERLAP_RATIO = 0.60;

    public List<RiderMatch> associate(
            List<DetectionInfo> motorcyclistList,
            List<DetectionInfo> helmetList,
            List<DetectionInfo> licensePlateList,
            int imageWidth,
            int imageHeight
    ) {
        List<RiderMatch> riderMatches = new ArrayList<>();

        for (DetectionInfo motorcyclist : motorcyclistList) {
            riderMatches.add(new RiderMatch(motorcyclist));
        }

        associateHelmets(riderMatches, helmetList, imageWidth, imageHeight);

        associateLicensePlates(riderMatches, licensePlateList, imageWidth, imageHeight);

        return riderMatches.stream()
                .filter(match -> !match.isDiscarded())
                .toList();
    }

    @Data
    public static class RiderMatch {
        private final DetectionInfo motorcyclist;
        private final List<DetectionInfo> helmets = new ArrayList<>();
        private DetectionInfo licensePlate;
        private boolean discarded;
    }

    private void associateHelmets(
            List<RiderMatch> riderMatches,
            List<DetectionInfo> helmetList,
            int imageWidth,
            int imageHeight
    ) {
        for (DetectionInfo helmet : helmetList) {
            for (RiderMatch riderMatch : riderMatches) {
                if (riderMatch.isDiscarded()) {
                    continue;
                }

                if (belongsHelmetToRider(helmet, riderMatch.getMotorcyclist(), imageWidth, imageHeight)) {
                    riderMatch.getHelmets().add(helmet);
                }
            }
        }
    }

    private void associateLicensePlates(
            List<RiderMatch> riderMatches,
            List<DetectionInfo> licensePlateList,
            int imageWidth,
            int imageHeight
    ) {
        for (DetectionInfo licensePlate : licensePlateList) {
            List<RiderMatch> candidates = new ArrayList<>();

            for (RiderMatch riderMatch : riderMatches) {
                if (riderMatch.isDiscarded()) {
                    continue;
                }

                if (belongsPlateToRider(licensePlate, riderMatch.getMotorcyclist(), imageWidth, imageHeight)) {
                    candidates.add(riderMatch);
                }
            }

            if (candidates.isEmpty()) {
                continue;
            }

            if (candidates.size() > 1) {
                candidates.forEach(candidate -> {
                    candidate.setDiscarded(true);
                    candidate.setLicensePlate(null);
                });
                continue;
            }

            RiderMatch owner = candidates.get(0);

            if (owner.getLicensePlate() != null) {
                owner.setDiscarded(true);
                owner.setLicensePlate(null);
                continue;
            }

            owner.setLicensePlate(licensePlate);
        }
    }

    private boolean belongsHelmetToRider(
            DetectionInfo helmet,
            DetectionInfo motorcyclist,
            int imageWidth,
            int imageHeight
    ) {
        Box riderBox = expandBox(toBox(motorcyclist), RIDER_EXPAND_RATIO_X, RIDER_EXPAND_RATIO_Y, imageWidth, imageHeight);
        Box helmetBox = toBox(helmet);

        if (!isCenterInUpperRegion(helmetBox, riderBox, HELMET_MAX_CENTER_Y_RATIO)) {
            return false;
        }

        return intersectionOverSmallBoxRatio(helmetBox, riderBox) >= HELMET_MIN_OVERLAP_RATIO;
    }

    private boolean belongsPlateToRider(
            DetectionInfo licensePlate,
            DetectionInfo motorcyclist,
            int imageWidth,
            int imageHeight
    ) {
        Box riderBox = expandBox(toBox(motorcyclist), RIDER_EXPAND_RATIO_X, RIDER_EXPAND_RATIO_Y, imageWidth, imageHeight);
        Box plateBox = toBox(licensePlate);

        if (!isCenterInLowerRegion(plateBox, riderBox, PLATE_MIN_CENTER_Y_RATIO)) {
            return false;
        }

        return intersectionOverSmallBoxRatio(plateBox, riderBox) >= PLATE_MIN_OVERLAP_RATIO;
    }

    @Data
    @AllArgsConstructor
    private static class Box {
        private final double left;
        private final double top;
        private final double right;
        private final double bottom;

        public double width() {
            return right - left;
        }

        public double height() {
            return bottom - top;
        }

        public double centerX() {
            return (left + right) / 2.0;
        }

        public double centerY() {
            return (top + bottom) / 2.0;
        }

        public double area() {
            return Math.max(0, width()) * Math.max(0, height());
        }
    }

    private Box toBox(DetectionInfo detectionInfo) {
        DetectionRectangle rect = detectionInfo.getDetectionRectangle();

        double left = rect.getX();
        double top = rect.getY();
        double right = rect.getX() + rect.getWidth();
        double bottom = rect.getY() + rect.getHeight();

        return new Box(left, top, right, bottom);
    }

    private Box expandBox(Box box, double expandRatioX, double expandRatioY, int imageWidth, int imageHeight) {
        double expandX = box.width() * expandRatioX;
        double expandY = box.height() * expandRatioY;

        double left = Math.max(0, box.getLeft() - expandX);
        double top = Math.max(0, box.getTop() - expandY);
        double right = Math.min(imageWidth, box.getRight() + expandX);
        double bottom = Math.min(imageHeight, box.getBottom() + expandY);

        return new Box(left, top, right, bottom);
    }

    private boolean isCenterInUpperRegion(Box childBox, Box riderBox, double maxCenterYRatio) {
        double centerX = childBox.centerX();
        double centerY = childBox.centerY();
        double maxY = riderBox.getTop() + riderBox.height() * maxCenterYRatio;

        return centerX >= riderBox.getLeft()
                && centerX <= riderBox.getRight()
                && centerY >= riderBox.getTop()
                && centerY <= maxY;
    }

    private boolean isCenterInLowerRegion(Box childBox, Box riderBox, double minCenterYRatio) {
        double centerX = childBox.centerX();
        double centerY = childBox.centerY();
        double minY = riderBox.getTop() + riderBox.height() * minCenterYRatio;

        return centerX >= riderBox.getLeft()
                && centerX <= riderBox.getRight()
                && centerY >= minY
                && centerY <= riderBox.getBottom();
    }

    private double intersectionOverSmallBoxRatio(Box smallBox, Box bigBox) {
        double interArea = intersectionArea(smallBox, bigBox);
        double smallArea = smallBox.area();

        if (smallArea <= 0) {
            return 0;
        }

        return interArea / smallArea;
    }

    private double intersectionArea(Box a, Box b) {
        double left = Math.max(a.getLeft(), b.getLeft());
        double top = Math.max(a.getTop(), b.getTop());
        double right = Math.min(a.getRight(), b.getRight());
        double bottom = Math.min(a.getBottom(), b.getBottom());

        double width = Math.max(0, right - left);
        double height = Math.max(0, bottom - top);

        return width * height;
    }
}
