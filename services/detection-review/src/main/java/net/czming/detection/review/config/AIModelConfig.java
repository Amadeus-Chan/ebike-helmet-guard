package net.czming.detection.review.config;

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
import lombok.RequiredArgsConstructor;
import net.czming.detection.review.properties.DetectionModeProperties;
import net.czming.detection.review.properties.ModelPathProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class AIModelConfig {


    private final ModelPathProperties modelPathProperties;


    @Bean(destroyMethod = "")
    public DetectorModel getDetectorModel() {
        DetectorModelConfig config = new DetectorModelConfig();
        config.setModelEnum(DetectorModelEnum.YOLOV8_CUSTOM_ONNX);
        config.setModelPath(modelPathProperties.getDetectModel());


        config.setDevice(DeviceEnum.GPU);
        config.setGpuId(0);


        return ObjectDetectionModelFactory.getInstance().getModel(config);
    }

    @Bean(destroyMethod = "")
    public OcrCommonRecModel getRecModel() {
        OcrRecModelConfig config = new OcrRecModelConfig();
        config.setRecModelEnum(CommonRecModelEnum.PP_OCR_V5_SERVER_REC_MODEL);
        config.setRecModelPath(modelPathProperties.getOcrRecognizeModel());


        config.setDevice(DeviceEnum.GPU);
        config.setGpuId(0);


        config.setTextDetModel(getDetectionModel());
        config.setDirectionModel(getDirectionModel());
        return OcrModelFactory.getInstance().getRecModel(config);
    }


    private OcrCommonDetModel getDetectionModel() {
        OcrDetModelConfig config = new OcrDetModelConfig();
        config.setModelEnum(CommonDetModelEnum.PP_OCR_V5_SERVER_DET_MODEL);
        config.setDetModelPath(modelPathProperties.getOcrDetectionModel());


        config.setDevice(DeviceEnum.GPU);
        config.setGpuId(0);


        return OcrModelFactory.getInstance().getDetModel(config);
    }

    private OcrDirectionModel getDirectionModel() {
        DirectionModelConfig config = new DirectionModelConfig();
        config.setModelEnum(DirectionModelEnum.PP_LCNET_X1_0);
        config.setModelPath(modelPathProperties.getOcrTextlineModel());


        config.setDevice(DeviceEnum.GPU);
        config.setGpuId(0);


        return OcrModelFactory.getInstance().getDirectionModel(config);
    }

}
