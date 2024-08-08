package com.drajer.ecrapp.model;

public class RXNTRrReceiverRequest {
    /** The FHIR Server for the EHR initiating the launch. */
    private String RrId;
    private String EncounterId;
    private String PatientId;
    private String RrXml;

    public RXNTRrReceiverRequest(String rrId, String encounterId, String patientId, String rrXml) {
        EncounterId = encounterId;
        PatientId = patientId;
        RrXml = rrXml;
        RrId = rrId;
    }

    public String getEncounterId() {
        return EncounterId;
    }

    public String getPatientId() {
        return PatientId;
    }

    public String getRrXml() {
        return RrXml;
    }

    public String getRrId() {
        return RrId;
    }
}
