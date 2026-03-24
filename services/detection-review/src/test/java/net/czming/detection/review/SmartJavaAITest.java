package net.czming.detection.review;

import ai.djl.modality.cv.Image;
import cn.smartjavaai.common.cv.SmartImageFactory;
import cn.smartjavaai.common.entity.DetectionInfo;
import cn.smartjavaai.common.entity.DetectionRectangle;
import cn.smartjavaai.common.entity.DetectionResponse;
import cn.smartjavaai.objectdetection.config.DetectorModelConfig;
import cn.smartjavaai.objectdetection.enums.DetectorModelEnum;
import cn.smartjavaai.objectdetection.model.DetectorModel;
import cn.smartjavaai.objectdetection.model.ObjectDetectionModelFactory;
import cn.smartjavaai.ocr.config.OcrRecOptions;
import cn.smartjavaai.ocr.model.common.recognize.OcrCommonRecModel;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class SmartJavaAITest {

    @Autowired
    DetectorModel detectorModel;

    @Autowired
    OcrCommonRecModel ocrCommonRecModel;

    public enum DetectionTypeEnum {
        motorcyclist,
        helmet,
        license_plate
    }

    @Data
    public static class RiderResult {
        private DetectionInfo motorcyclist;
        private boolean hasHelmet;
        private String licensePlate;
    }

    @Test
    public void detect() throws IOException {
/*        DetectorModelConfig config = new DetectorModelConfig();
        config.setModelEnum(DetectorModelEnum.YOLOV8_CUSTOM_ONNX);
        config.setModelPath("src/main/resources/model/detect_model/best.onnx");
        DetectorModel detectorModel = ObjectDetectionModelFactory.getInstance().getModel(config);*/
        Image image = SmartImageFactory.getInstance().fromFile(Paths.get("src/main/resources/img.jpg"));


        System.out.println("Image loaded: " + image);

        DetectionResponse detectResponse = detectorModel.detect(image);
        System.out.println("Detection Response: " + detectResponse);

        // 分离检测结果
        ArrayList<DetectionInfo> motorcyclistList = new ArrayList<>();
        ArrayList<DetectionInfo> helmetList = new ArrayList<>();
        ArrayList<DetectionInfo> licensePlateList = new ArrayList<>();

        detectResponse.getDetectionInfoList().forEach(detectionInfo -> {
            String type = detectionInfo.getObjectDetInfo().getClassName();
            switch (type) {
                case "motorcyclist" -> motorcyclistList.add(detectionInfo);
                case "helmet" -> helmetList.add(detectionInfo);
                case "license_plate" -> licensePlateList.add(detectionInfo);
            }
        });

        // 打印分离结果
        System.out.println("\nMotorcyclist Detection:");
        motorcyclistList.forEach(System.out::println);
        System.out.println("\nHelmet Detection:");
        helmetList.forEach(System.out::println);
        System.out.println("\nLicense Plate Detection:");
        licensePlateList.forEach(System.out::println);

    }

    /**
     * 计算两个检测框的IoU (Intersection over Union)
     */
    private double calculateIoU(DetectionRectangle rect1, DetectionRectangle rect2) {
        int x1 = Math.max(rect1.getX(), rect2.getX());
        int y1 = Math.max(rect1.getY(), rect2.getY());
        int x2 = Math.min(rect1.getX() + rect1.getWidth(), rect2.getX() + rect2.getWidth());
        int y2 = Math.min(rect1.getY() + rect1.getHeight(), rect2.getY() + rect2.getHeight());

        int intersectionArea = Math.max(0, x2 - x1) * Math.max(0, y2 - y1);
        int unionArea = rect1.getWidth() * rect1.getHeight() +
                rect2.getWidth() * rect2.getHeight() -
                intersectionArea;

        return (double) intersectionArea / unionArea;
    }

    /**
     * Rider Non-Overlap Filter: 舍弃所有重叠的摩托车手检测框 (IoU > 0.3)
     */
    private ArrayList<DetectionInfo> nonOverlapFilter(ArrayList<DetectionInfo> detectionInfoList) {
        ArrayList<DetectionInfo> filtered = new ArrayList<>();
        for (int i = 0; i < detectionInfoList.size(); i++) {
            DetectionInfo rider1 = detectionInfoList.get(i);
            boolean isOverlap = false;

            for (int j = 0; j < detectionInfoList.size(); j++) {
                if (i == j) continue;

                DetectionInfo rider2 = detectionInfoList.get(j);
                double iou = calculateIoU(
                        rider1.getDetectionRectangle(),
                        rider2.getDetectionRectangle()
                );

                if (iou > 0.3) { // 重叠阈值 0.3
                    isOverlap = true;
                    break;
                }
            }

            if (!isOverlap) {
                filtered.add(rider1);
            }
        }
        return filtered;
    }

    /**
     * 关联摩托车手、头盔和车牌
     */
    private List<RiderResult> associateRiderAttributes(
            ArrayList<DetectionInfo> riders,
            ArrayList<DetectionInfo> helmets,
            ArrayList<DetectionInfo> plates
    ) {
        List<RiderResult> results = new ArrayList<>();

        for (DetectionInfo rider : riders) {
            RiderResult result = new RiderResult();
            result.setMotorcyclist(rider);
            result.setHasHelmet(false);
            result.setLicensePlate(null);

            // 检查头盔归属
            for (DetectionInfo helmet : helmets) {
                if (calculateIoU(helmet.getDetectionRectangle(), rider.getDetectionRectangle()) > 0) {
                    result.setHasHelmet(true);
                    break;
                }
            }

            // 检查车牌归属
            for (DetectionInfo plate : plates) {
                if (calculateIoU(plate.getDetectionRectangle(), rider.getDetectionRectangle()) > 0) {
                    // 实际项目中这里应该处理车牌识别结果
                    result.setLicensePlate("车牌识别结果"); // 实际应为plate.getLicensePlate()
                    break;
                }
            }

            results.add(result);
        }
        return results;
    }

    @Test
    public void test() throws IOException {
        SmartImageFactory.setEngine(SmartImageFactory.Engine.OPENCV);
        Image image = SmartImageFactory.getInstance().fromFile("src/main/resources/img.png");
        System.out.println(ocrCommonRecModel.recognize(image, new OcrRecOptions()));
    }
}