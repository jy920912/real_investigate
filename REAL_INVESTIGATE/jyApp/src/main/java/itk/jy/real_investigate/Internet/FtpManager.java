package itk.jy.real_investigate.Internet;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;

public class FtpManager {
    private FTPClient mFTPClient;

    public FtpManager() {
        mFTPClient = new FTPClient();
    }

    //ftp 접속
    public boolean ftpConnect(String host, String username, String password, int port) {
        boolean result = false;
        try{
            mFTPClient.setControlEncoding("UTF-8");
            mFTPClient.connect(host,port);

            if(FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())) {
                result = mFTPClient.login(username,password);
                mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
                mFTPClient.enterLocalPassiveMode();

            }
        } catch (Exception e){
            e.getStackTrace();
        }
        return result;
    }

    //ftp 접속해제
    public void ftpDisconnect() {
        try{
            mFTPClient.logout();
            mFTPClient.disconnect();
        } catch (Exception e){
            e.getStackTrace();
        }
    }

    //ftp 현재 디렉토리
    public String ftpGetDirectory(){
        String directory = null;
        try{
            directory = mFTPClient.printWorkingDirectory();
        } catch (Exception e){
            e.getStackTrace();
        }
        return directory;
    }
    //디렉토리 생성
    public boolean ftpMakeDirectory(String directory) {
        try{
            mFTPClient.makeDirectory(directory);
            return true;
        }catch (Exception e) { return false; }
    }

    //디렉토리 변경
    public boolean ftpChangeDir(String directory) {
        try{
            mFTPClient.changeWorkingDirectory(directory);
            return true;
        }catch (Exception e) { return false; }
    }

    //파일 업로드
    public boolean ftpUploadFile(String filePath, String desFileName, String desDirectory) {
        boolean result = false;
        try{
            FileInputStream fis = new FileInputStream(filePath);
            if(ftpChangeDir(desDirectory)) {
                result = mFTPClient.storeFile(desFileName, fis);
            }
            fis.close();
        } catch (Exception e){
            e.getStackTrace();
        }
        return result;
    }
}
