package net.czming.detection.review;

import cn.smartjavaai.common.enums.DeviceEnum;
import cn.smartjavaai.objectdetection.config.DetectorModelConfig;
import cn.smartjavaai.objectdetection.enums.DetectorModelEnum;
import cn.smartjavaai.objectdetection.model.DetectorModel;
import cn.smartjavaai.objectdetection.model.ObjectDetectionModelFactory;
import cn.smartjavaai.ocr.config.DirectionModelConfig;
import cn.smartjavaai.ocr.config.OcrDetModelConfig;
import cn.smartjavaai.ocr.config.OcrRecModelConfig;
import cn.smartjavaai.ocr.enums.CommonDetModelEnum;
import cn.smartjavaai.ocr.enums.CommonRecModelEnum;
import cn.smartjavaai.ocr.enums.DirectionModelEnum;
import cn.smartjavaai.ocr.factory.OcrModelFactory;
import cn.smartjavaai.ocr.model.common.detect.OcrCommonDetModel;
import cn.smartjavaai.ocr.model.common.direction.OcrDirectionModel;
import cn.smartjavaai.ocr.model.common.recognize.OcrCommonRecModel;

public class SmartJavaAiGpuCloseTest {




    private static final String DETECT_MODEL_PATH =
            "C:\\Users\\likeai\\Desktop\\ebike-helmet-guard\\services\\detection-review\\src\\main\\resources\\model\\detect_model\\best.onnx";

    private static final String OCR_DET_MODEL_PATH =
            "C:\\Users\\likeai\\Desktop\\ebike-helmet-guard\\services\\detection-review\\src\\main\\resources\\model\\ocr_detection_model\\PP-OCRv5_server_det.onnx";

    private static final String OCR_REC_MODEL_PATH =
            "C:\\Users\\likeai\\Desktop\\ebike-helmet-guard\\services\\detection-review\\src\\main\\resources\\model\\ocr_recognize_model\\PP-OCRv5_server_rec.onnx";

    private static final String OCR_DIRECTION_MODEL_PATH =
            "C:\\Users\\likeai\\Desktop\\ebike-helmet-guard\\services\\detection-review\\src\\main\\resources\\model\\ocr_textline_model\\PP-LCNet_x1_0_textline_ori_infer.onnx";

    public static void main(String[] args) {
        printPathCheck();


//        testDetectorClose();
//        testOcrDetClose();
//        testOcrDirectionClose();
        testOcrRecWithChildrenClose();
    }


    private static void testDetectorClose() {
        DetectorModel detectorModel = null;
        try {
            System.out.println("==== testDetectorClose: creating detector model ====");
            detectorModel = createDetectorModel();
            System.out.println("==== testDetectorClose: detector model created ====");
        } catch (Exception e) {
            System.err.println("==== testDetectorClose: create failed ====");
            e.printStackTrace();
        } finally {
            System.out.println("==== testDetectorClose: begin close detector model ====");
            closeQuietly(detectorModel, "DetectorModel");
            System.out.println("==== testDetectorClose: finished ====");
        }
    }

    private static void testOcrDetClose() {
        OcrCommonDetModel detModel = null;
        try {
            System.out.println("==== testOcrDetClose: creating OCR det model ====");
            detModel = createOcrDetModel();
            System.out.println("==== testOcrDetClose: OCR det model created ====");
        } catch (Exception e) {
            System.err.println("==== testOcrDetClose: create failed ====");
            e.printStackTrace();
        } finally {
            System.out.println("==== testOcrDetClose: begin close OCR det model ====");
            closeQuietly(detModel, "OcrCommonDetModel");
            System.out.println("==== testOcrDetClose: finished ====");
        }
    }

    private static void testOcrDirectionClose() {
        OcrDirectionModel directionModel = null;
        try {
            System.out.println("==== testOcrDirectionClose: creating OCR direction model ====");
            directionModel = createOcrDirectionModel();
            System.out.println("==== testOcrDirectionClose: OCR direction model created ====");
        } catch (Exception e) {
            System.err.println("==== testOcrDirectionClose: create failed ====");
            e.printStackTrace();
        } finally {
            System.out.println("==== testOcrDirectionClose: begin close OCR direction model ====");
            closeQuietly(directionModel, "OcrDirectionModel");
            System.out.println("==== testOcrDirectionClose: finished ====");
        }
    }


    private static void testOcrRecWithChildrenClose() {
        OcrCommonDetModel detModel = null;
        OcrDirectionModel directionModel = null;
        OcrCommonRecModel recModel = null;

        try {
            System.out.println("==== testOcrRecWithChildrenClose: creating OCR det model ====");
            detModel = createOcrDetModel();
            System.out.println("==== testOcrRecWithChildrenClose: OCR det model created ====");

            System.out.println("==== testOcrRecWithChildrenClose: creating OCR direction model ====");
            directionModel = createOcrDirectionModel();
            System.out.println("==== testOcrRecWithChildrenClose: OCR direction model created ====");

            System.out.println("==== testOcrRecWithChildrenClose: creating OCR rec model ====");
            recModel = createOcrRecModel(detModel, directionModel);
            System.out.println("==== testOcrRecWithChildrenClose: OCR rec model created ====");
        } catch (Exception e) {
            System.err.println("==== testOcrRecWithChildrenClose: create failed ====");
            e.printStackTrace();
        } finally {
            System.out.println("==== testOcrRecWithChildrenClose: begin close OCR rec model ====");
            closeQuietly(recModel, "OcrCommonRecModel");

            System.out.println("==== testOcrRecWithChildrenClose: begin close OCR det model ====");
            closeQuietly(detModel, "OcrCommonDetModel");

            System.out.println("==== testOcrRecWithChildrenClose: begin close OCR direction model ====");
            closeQuietly(directionModel, "OcrDirectionModel");

            System.out.println("==== testOcrRecWithChildrenClose: finished ====");
        }
    }

    private static DetectorModel createDetectorModel() {
        DetectorModelConfig config = new DetectorModelConfig();
        config.setModelEnum(DetectorModelEnum.YOLOV8_CUSTOM_ONNX);
        config.setModelPath(DETECT_MODEL_PATH);
        config.setDevice(DeviceEnum.GPU);
        config.setGpuId(0);
        return ObjectDetectionModelFactory.getInstance().getModel(config);
    }

    private static OcrCommonDetModel createOcrDetModel() {
        OcrDetModelConfig config = new OcrDetModelConfig();
        config.setModelEnum(CommonDetModelEnum.PP_OCR_V5_SERVER_DET_MODEL);
        config.setDetModelPath(OCR_DET_MODEL_PATH);
        config.setDevice(DeviceEnum.GPU);
        config.setGpuId(0);
        return OcrModelFactory.getInstance().getDetModel(config);
    }

    private static OcrDirectionModel createOcrDirectionModel() {
        DirectionModelConfig config = new DirectionModelConfig();
        config.setModelEnum(DirectionModelEnum.PP_LCNET_X1_0);
        config.setModelPath(OCR_DIRECTION_MODEL_PATH);
        config.setDevice(DeviceEnum.GPU);
        config.setGpuId(0);
        return OcrModelFactory.getInstance().getDirectionModel(config);
    }

    private static OcrCommonRecModel createOcrRecModel(OcrCommonDetModel detModel, OcrDirectionModel directionModel) {
        OcrRecModelConfig config = new OcrRecModelConfig();
        config.setRecModelEnum(CommonRecModelEnum.PP_OCR_V5_SERVER_REC_MODEL);
        config.setRecModelPath(OCR_REC_MODEL_PATH);
        config.setDevice(DeviceEnum.GPU);
        config.setGpuId(0);
        config.setTextDetModel(detModel);
        config.setDirectionModel(directionModel);
        return OcrModelFactory.getInstance().getRecModel(config);
    }

    private static void printPathCheck() {
        System.out.println("==== path check ====");
        System.out.println("DETECT_MODEL_PATH exists = " + new java.io.File(DETECT_MODEL_PATH).exists());
        System.out.println("OCR_DET_MODEL_PATH exists = " + new java.io.File(OCR_DET_MODEL_PATH).exists());
        System.out.println("OCR_REC_MODEL_PATH exists = " + new java.io.File(OCR_REC_MODEL_PATH).exists());
        System.out.println("OCR_DIRECTION_MODEL_PATH exists = " + new java.io.File(OCR_DIRECTION_MODEL_PATH).exists());
        System.out.println("====================");
    }

    private static void closeQuietly(AutoCloseable closeable, String name) {
        if (closeable == null) {
            System.out.println(name + " is null, skip close");
            return;
        }
        try {
            closeable.close();
            System.out.println(name + " closed successfully");
        } catch (Throwable t) {
            System.err.println(name + " close failed");
            t.printStackTrace();
        }
    }
}