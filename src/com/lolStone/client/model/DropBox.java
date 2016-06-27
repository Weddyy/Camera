package com.lolStone.client.model;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.*;

import java.io.*;
import java.util.Date;
import java.util.Locale;

/**
 * Created by root on 01.03.16.
 */
public class DropBox {
    private static final String key="NotForGit";

    public void DownloadFiles(String patchTo,String filter,String url) throws Exception
    {
        String userLocale = Locale.getDefault().toString();
        DbxRequestConfig requestConfig = new DbxRequestConfig("sts-downloader", userLocale);
        DbxClientV2 dbxClient = new DbxClientV2(requestConfig, key);
        SearchResult res = dbxClient.files.search(url,filter);

        for(SearchMatch data:res.getMatches())
        {
            DbxDownloader<FileMetadata> f =dbxClient.files.download(data.getMetadata().getPathDisplay());
            String[] pat=data.getMetadata().getPathDisplay().split("/");
            File dir=new File(patchTo);
            if(!dir.exists())
                dir.mkdirs();
            OutputStream o=new FileOutputStream(patchTo+"/"+pat[pat.length-1]);
            f.download(o);
        }

    }

    public void UploadFile(String path,String name) throws Exception
    {
        String userLocale = Locale.getDefault().toString();
        DbxRequestConfig requestConfig = new DbxRequestConfig("Camera-uploader", userLocale);
        DbxClientV2 dbxClient = new DbxClientV2(requestConfig, key);

        File localFile = new File(path);
        FileMetadata metadata;
        InputStream in = new FileInputStream(localFile);
        try {
            metadata = dbxClient.files.uploadBuilder(name)
                    .withMode(WriteMode.ADD)
                    .withClientModified(new Date(localFile.lastModified()))
                    .uploadAndFinish(in);
        } finally {
            in.close();
        }

    }
}
