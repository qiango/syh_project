package com.syhdoctor.webserver.api.response;

import com.alibaba.fastjson.annotation.JSONField;

public class DoctorBasicInfoResponse extends BaseResponse {

    @JSONField(name = "doc_multi_sited_lic_record_status")
    private String docMultiSitedLicRecordStatus;

    @JSONField(name = "id_card_status")
    private String idCardStatus;

    @JSONField(name = "cert_doc_prac_status")
    private String certDocPracStatus;

    @JSONField(name = "title_cert_status")
    private String titleCertStatus;

    @JSONField(name = "doc_cert_status")
    private String docCertStatus;

    @JSONField(name = "doc_photo_status")
    private String docPhotoStatus;

    @JSONField(name = "employ_file_status")
    private String employFileStatus;
}
