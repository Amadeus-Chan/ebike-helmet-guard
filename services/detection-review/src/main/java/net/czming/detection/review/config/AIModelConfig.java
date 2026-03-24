package net.czming.detection.review.config;

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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AIModelConfig {


    @Bean
    public DetectorModel getDetectorModel(){
        DetectorModelConfig config = new DetectorModelConfig();
        config.setModelEnum(DetectorModelEnum.YOLOV8_CUSTOM_ONNX);
        config.setModelPath("C:\\Users\\likeai\\Desktop\\ebike-helmet-guard\\services\\detection-review\\src\\main\\resources\\model\\detect_model\\best.onnx");
        return ObjectDetectionModelFactory.getInstance().getModel(config);
    }

    @Bean
    public OcrCommonRecModel getProRecModel() {
        OcrRecModelConfig recModelConfig = new OcrRecModelConfig();
        //指定文本识别模型，切换模型需要同时修改modelEnum及modelPath
        recModelConfig.setRecModelEnum(CommonRecModelEnum.PP_OCR_V5_SERVER_REC_MODEL);
        recModelConfig.setRecModelPath("C:\\Users\\likeai\\Desktop\\ebike-helmet-guard\\services\\detection-review\\src\\main\\resources\\model\\ocr_recognize_model\\PP-OCRv5_server_rec.onnx");
        //指定识别模型位置，需要更改为自己的模型路径（下载地址请查看文档）
        recModelConfig.setTextDetModel(getProDetectionModel());
        recModelConfig.setDirectionModel(getDirectionModel());
        return OcrModelFactory.getInstance().getRecModel(recModelConfig);
    }


    private OcrCommonDetModel getProDetectionModel() {
        OcrDetModelConfig config = new OcrDetModelConfig();
        //指定检测模型，切换模型需要同时修改modelEnum及modelPath
        config.setModelEnum(CommonDetModelEnum.PP_OCR_V5_SERVER_DET_MODEL);
        //指定模型位置，需要更改为自己的模型路径（下载地址请查看文档）
        config.setDetModelPath("C:\\Users\\likeai\\Desktop\\ebike-helmet-guard\\services\\detection-review\\src\\main\\resources\\model\\ocr_detection_model\\PP-OCRv5_server_det.onnx");
        return OcrModelFactory.getInstance().getDetModel(config);
    }

    private OcrDirectionModel getDirectionModel() {
        DirectionModelConfig directionModelConfig = new DirectionModelConfig();
        //指定行文本方向检测模型，切换模型需要同时修改modelEnum及modelPath
        directionModelConfig.setModelEnum(DirectionModelEnum.PP_LCNET_X1_0);
        //指定行文本方向检测模型路径，需要更改为自己的模型路径（下载地址请查看文档）
        directionModelConfig.setModelPath("C:\\Users\\likeai\\Desktop\\ebike-helmet-guard\\services\\detection-review\\src\\main\\resources\\model\\ocr_textline_model\\PP-LCNet_x1_0_textline_ori_infer.onnx");
        return OcrModelFactory.getInstance().getDirectionModel(directionModelConfig);
    }

}
