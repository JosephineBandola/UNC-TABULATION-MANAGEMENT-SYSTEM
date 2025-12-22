package ph.edu.unc.pageantms.model;

public class CandidateModel {
    private String candidateId;
    private String represents;
    private int candidateNo;

    public CandidateModel(String candidateId, String represents, int candidateNo) {
        this.candidateId = candidateId;
        this.represents = represents;
        this.candidateNo = candidateNo;
    }

    public String getCandidateId() { return candidateId; }
    public String getRepresents() { return represents; }
    public int getCandidateNo() { return candidateNo; }
}