package com.drajer.ecrapp.model;

public class RXNTRrReceiverRequest {
    /** The FHIR Server for the EHR initiating the launch. */
    private int EncounterId;
    private int PatientId;
    private String RrXml;

    public RXNTRrReceiverRequest(int encounterId, int patientId, String rrXml) {
        EncounterId = encounterId;
        PatientId = patientId;
        RrXml = rrXml;
    }
}
