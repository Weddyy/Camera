package com.lolStone.client.model.API;

import com.lolStone.client.model.*;
import enums.UploadClient;
import model.GameChunk;
import model.GameInfo;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by root on 10.02.16.
 */
public class GameAPI {
    Loggs _log = new Loggs();
    GameInfo _info;
    URL urlChunk, urlKey;
    int indexChunk = 0, indexKey = 0;
    boolean isNeedDownloadChunk=true,isNeedDownloadKey=true;
    DropBox _downClient=null;

    public GameAPI(GameInfo info) {
        _info = info;
        if(info.UploaderType()== UploadClient.DROPBOX)
            _downClient=new DropBox();
    }

    private HttpURLConnection httpWork(String urlR) throws Exception {
        URL url = new URL(urlR);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        return conn;
    }

    public boolean saveMeta() {
        try {
            HttpURLConnection conn = httpWork("http://" + _info.getUrl() + "/observer-mode/rest/consumer/getGameMetaData/" + _info.getPrefix() + "/" + _info.get_idGame() + "/1/token");

            try {
                if (conn.getResponseCode() == 200) {

                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            (conn.getInputStream())));
                    String tet = "";
                    String output;
                    while ((output = br.readLine()) != null) {
                        tet += output;
                    }
                    File theFile = new File(_info.get_tmpPatch());
                    if (!theFile.exists())
                        theFile.mkdirs();

                    try (FileWriter writer = new FileWriter(_info.get_tmpPatch() + "/Meta", false)) {
                        writer.write(tet);
                        writer.flush();
                    } catch (IOException ex) {
                        System.out.println(ex.getMessage());
                        return false;
                    }
                    _downClient.UploadFile(_info.get_tmpPatch() + "/Meta",_info.get_MainPath() + "/Meta");
                    new File(_info.get_tmpPatch()+ "/Meta").delete();
                    return true;
                } else {
                    System.out.println("Cannot get Meta data for game " + _info.get_idGame() + " server " + _info.getServerId().toString());
                    return false;
                }
            } finally {
                conn.disconnect();
            }

        } catch (Exception e) {

            _log.info(e);
            return false;
        }
    }

    /**
     *
     * @return
     * @throws Exception Internet exception (download file,load url)
     */
    public GameChunk getChankGame() throws Exception {
        HttpURLConnection conn = httpWork("http://" + _info.getUrl() + "/observer-mode/rest/consumer/getLastChunkInfo/" + _info.getPrefix() + "/" + _info.get_idGame() + "/1/token");

        String tet = "";
        if (conn.getResponseCode() == 200) {

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));
            String output;
            while ((output = br.readLine()) != null) {
                tet += output;
            }

            conn.disconnect();
            System.out.println(tet);
            GameChunk retSpeak = JSONParser.getGameInfo(tet);

            if (retSpeak.getEndGameChunkId() != 0 && retSpeak.getNextAvailableChunk() == 0) {
                try (FileWriter writer = new FileWriter(_info.get_tmpPatch() + "/LastChunk", false)) {
                    writer.write(tet);
                    writer.flush();
                } catch (Exception e) {
                    _log.info(e);
                }
                _downClient.UploadFile(_info.get_tmpPatch() + "/LastChunk",_info.get_MainPath() + "/LastChunk");
                new File(_info.get_tmpPatch()+ "/LastChunk").delete();
            }

            return retSpeak;
        }

        conn.disconnect();

        throw new Exception("ERROR not found game info or request != 400");
    }

    public int downloadGame(){

        GameChunk gameChunk;

        try {
            gameChunk = getChankGame();
            _info.setLastChunk(gameChunk.getJso());
        } catch (FileNotFoundException e) {
            System.out.println("Download File Chunk not found.");
            return 2000;
        }catch (Exception e)
        {
            _log.info(e);
            return 10000;
        }

        int downloadChunkDelay = -1;
        int downloadKeyDelay = -1;

        if(isNeedDownloadChunk) {
            try {
                downloadChunkDelay = downloadChunk(gameChunk);
            } catch (FileNotFoundException e) {
                System.out.println("Download File Chung game index " + indexChunk + " not found.");
                downloadChunkDelay=2000;
            }
            catch (Exception e)
            {
                _log.info(e);
                downloadChunkDelay = 10000;
            }
        }

        if(isNeedDownloadKey) {
            try {

                downloadKeyDelay = downloadKey(gameChunk);
            } catch (FileNotFoundException e) {
                System.out.println("Download File Key game index " + indexKey + " not found.");
                downloadKeyDelay=2000;
            }
            catch (Exception e)
            {
                _log.info(e);
                downloadKeyDelay = 10000;
            }
        }

        isNeedDownloadChunk=downloadChunkDelay!=-1;
        isNeedDownloadKey=downloadKeyDelay!=-1;

        if(downloadChunkDelay==-1 && downloadKeyDelay==-1)
            return -1;

        if(downloadChunkDelay==-1)
            return downloadKeyDelay;

        if(downloadKeyDelay!=-1)
            return downloadChunkDelay;

        return downloadChunkDelay<downloadKeyDelay?downloadChunkDelay:downloadKeyDelay;
    }

    /**
     *
     * @param gameChunk
     * @return time to delay
     * @throws Exception Internet exception (download file,load url)
     */
    private int downloadChunk(GameChunk gameChunk) throws Exception {
        if(gameChunk==null)
        {
            //Why so big ? lol API key is limited
            return 10000;
        }

        if (gameChunk.getEndGameChunkId() != 0 && gameChunk.getEndGameChunkId() == indexChunk && gameChunk.getNextAvailableChunk() == 0) {
            saveGameEndInfo();
            return -1;
        }
        if (gameChunk.getChunkId() == indexChunk) {
            if (gameChunk.getNextAvailableChunk() > 0)
                return gameChunk.getNextAvailableChunk();
            else
                return 10000;
        }
        indexChunk++;

        urlChunk = new URL("http://" + _info.getUrl() + "/observer-mode/rest/consumer/getGameDataChunk/" + _info.getPrefix() + "/" + _info.get_idGame() + "/" + indexChunk + "/token");
        BufferedInputStream bis = new BufferedInputStream(urlChunk.openStream());
        File theFile = new File(_info.get_tmpPatch() + "/Chunk/");
        if (!theFile.exists())
            theFile.mkdirs();
        FileOutputStream fis = new FileOutputStream(_info.get_tmpPatch() + "/Chunk/chunk" + indexChunk);
        byte[] buffer = new byte[1024];
        int count = 0;
        while ((count = bis.read(buffer, 0, 1024)) != -1) {
            fis.write(buffer, 0, count);
        }
        fis.close();
        bis.close();
        _downClient.UploadFile(_info.get_tmpPatch() + "/Chunk/chunk" + indexChunk,_info.get_MainPath() + "/Chunk/chunk" + indexChunk);
        new File(_info.get_tmpPatch() + "/Chunk/chunk" + indexChunk).delete();
        System.out.println("Download Chunk " + indexChunk);
        if (gameChunk.getChunkId() == indexChunk) {
            return gameChunk.getNextAvailableChunk() + 3000;
        } else {
            return 2000;
        }
    }

    /**
     *
     * @param gameChunk game for download key
     * @return time to delay
     * @throws Exception Internet exception (download file,load url)
     */
    private int downloadKey(GameChunk gameChunk) throws Exception {

        if(gameChunk==null)
        {
            return 10000;
        }

        if (gameChunk.getEndGameChunkId() != 0 && gameChunk.getKeyFrameId()==indexKey && gameChunk.getNextAvailableChunk()==0) {
            System.out.println("EXIT "+indexKey+" "+gameChunk.getKeyFrameId()+" "+gameChunk.getEndGameChunkId());
            return -1;
        }

        if(gameChunk.getKeyFrameId()==indexKey || gameChunk.getKeyFrameId()==0)
        {
            if(gameChunk.getNextAvailableChunk()>0)
                return gameChunk.getNextAvailableChunk();
            else
                return 10000;
        }
        indexKey++;

        urlKey = new URL("http://" + _info.getUrl() + "/observer-mode/rest/consumer/getKeyFrame/" + _info.getPrefix() + "/" + _info.get_idGame() + "/" + indexKey + "/token");
        BufferedInputStream bis = new BufferedInputStream(urlKey.openStream());
        File theFile = new File(_info.get_tmpPatch()+"/KeyFrame/");
        if (!theFile.exists())
            theFile.mkdirs();
        FileOutputStream fis = new FileOutputStream(_info.get_tmpPatch()+"/KeyFrame/keyFrame" + indexKey);
        byte[] buffer = new byte[1024];
        int count = 0;
        while ((count = bis.read(buffer, 0, 1024)) != -1) {
            fis.write(buffer, 0, count);
        }
        fis.close();
        bis.close();

        _downClient.UploadFile(_info.get_tmpPatch()+"/KeyFrame/keyFrame" + indexKey,_info.get_MainPath()+"/KeyFrame/keyFrame" + indexKey);
        new File(_info.get_tmpPatch() + "/KeyFrame/keyFrame" + indexKey).delete();

        System.out.println("Download keyFrame "+indexKey);

        if (gameChunk.getKeyFrameId() == indexKey) {
            return gameChunk.getNextAvailableChunk()+2000;
        }else{
            return 2000;
        }

    }

    private void saveGameEndInfo() {
        try {
            urlChunk = new URL("http://" + _info.getUrl() + "/observer-mode/rest/consumer/endOfGameStats/" + _info.getPrefix() + "/" + _info.get_idGame() + "/null");
            BufferedInputStream bis = new BufferedInputStream(urlChunk.openStream());
            File theFile = new File(_info.get_tmpPatch() + "/GameEndInfo/");
            if (!theFile.exists())
                theFile.mkdirs();
            FileOutputStream fis = new FileOutputStream(_info.get_tmpPatch() + "/GameEndInfo/info");
            byte[] buffer = new byte[1024];
            int count = 0;
            while ((count = bis.read(buffer, 0, 1024)) != -1) {
                fis.write(buffer, 0, count);
            }
            fis.close();
            bis.close();

            _downClient.UploadFile(_info.get_tmpPatch()+"/GameEndInfo/info",_info.get_MainPath()+"/GameEndInfo/info");
            new File(_info.get_tmpPatch() + "/GameEndInfo/info").delete();


        } catch (Exception e) {
            _log.info(e);
        }
    }


    public void clearTempDir()
    {
        File theFile = new File(_info.get_tmpPatch());
        if (theFile.exists())
            theFile.delete();
    }
}
