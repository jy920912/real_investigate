package itk.jy.real_investigate.PathList;

public class ListGetSet {
    private String FileName;
    private String FilePath;

    public ListGetSet(String FN, String FP) {
        FileName = FN;
        FilePath = FP;
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


}
