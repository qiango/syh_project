package com.syhdoctor.common.answer;

import com.syhdoctor.common.utils.EnumUtils.QAContentTypeEnum;

public class ProblemFactory {

    public static IProblem getProblemImpl(QAContentTypeEnum typeEnum) {
        IProblem problem;
        switch (typeEnum) {
            case DiseaseUser:
                problem = UserDiseaseTemplateServiceImpl.getInstance();
                break;
            case DiseaseDoctor:
                problem = DoctorDiseaseTemplateServiceImpl.getInstance();
                break;
            case Prescription:
                problem = PrescriptionServiceImpl.getInstance();
                break;
            case UserInfo:
                problem = UserInfoServiceImpl.getInstance();
                break;
            case Tips:
                problem = StartEndServiceImpl.getInstance();
                break;
            default:
                problem = UserDiseaseTemplateServiceImpl.getInstance();
                break;
        }
        return problem;
    }
}
