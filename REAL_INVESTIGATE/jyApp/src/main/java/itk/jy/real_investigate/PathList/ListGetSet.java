package itk.jy.real_investigate.PathList;

public class ListGetSet {
    private String FileName;
    private String FilePath;
    private int AorB;

    public ListGetSet(String FN, String FP, int AB) {
        FileName = FN;
        FilePath = FP;
        AorB = AB;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getFilePath() {
        return FilePath;
    }

    public void setFilePath(String filePath) {
        FilePath = filePath;
    }

    public int getAorB() {return AorB;}

    public void setAorB(int AB) {AorB = AB;}


}
