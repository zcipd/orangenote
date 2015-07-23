package com.orangenote.orangenote;

import android.content.Context;
import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketException;

/**
 * Created by mercurys on 15. 7. 18.
 */

public class FTPConnect {

    public FTPClient mFTPClient = null;

    public boolean ftpConnect(String host, String username, String password, int port)
    {
        try
        {
            mFTPClient = new FTPClient();
            mFTPClient.connect(host, port);
            if(FTPReply.isPositiveCompletion(mFTPClient.getReplyCode()))
            {
                boolean status = mFTPClient.login(username, password);
                mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
                mFTPClient.enterLocalPassiveMode();
            }
        }
        catch (SocketException e)
        {
            e.printStackTrace();
            Log.d("FTPConnect", "FTP 소켓 에러 라네요.");
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.d("FTPConnect", "FTP 접속 할 때 뭔가 터졌다네요.");
        }

        return true;
    }

    public boolean ftpDisconnect()
    {
        try
        {
            mFTPClient.logout();
            mFTPClient.disconnect();
        }
        catch (Exception e)
        {
            Log.d("FTPConnect", "FTP 로그아웃 할 때 뭔가 터졌다네요.");
        }

        return false;
    }

    public boolean ftpDownload()
    {
        return true;
    }

    public boolean ftpUpload(String srcFilePaht, String desFileName, String desDirectory, Context context)
    {
        boolean status = false;
        try
        {
            FileInputStream srcFileStream = new FileInputStream(srcFilePaht);
            status = mFTPClient.storeFile(desFileName, srcFileStream);
            srcFileStream.close();
            return status;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.d("FTPConnect", "FTP 업로드 할 때 뭔가 터졌다네요.");
        }
        return status;
    }

    public boolean MakeDirectory(String pathname)
    {
        try
        {
            mFTPClient.makeDirectory(pathname);
        }
        catch (Exception e)
        {
            Log.d("FTPConnect", "FTP에 폴더 만들 때 뭔가 터졌다네요.");
        }

        return true;
    }


}
