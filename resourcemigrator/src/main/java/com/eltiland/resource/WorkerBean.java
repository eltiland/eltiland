package com.eltiland.resource;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.CopyUtils;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: lexaux
 * Date: 2/8/13
 * Time: 3:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class WorkerBean
{
    private DataSource dataSource;

    private String rootFSDir;

    private File rootFilesystemDir;

    public static void log(String s)
    {
        System.out.println(s);
    }

    public DataSource getDataSource()
    {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    public String getRootFSDir()
    {
        return rootFSDir;
    }

    public void setRootFSDir(String rootFSDir)
    {
        this.rootFSDir = rootFSDir;
    }

    public void doJob()
    {
        Connection connection = null;
        ResultSet rs = null;

        int collisions = 0;
        try
        {
            connection = dataSource.getConnection();
            PreparedStatement getSingleFileToProcessStatement = connection.prepareStatement("select _id, body, filename from file_body where processed=false order by _id ");
            getSingleFileToProcessStatement.setMaxRows(1);

            PreparedStatement markProcessedStatement = connection.prepareStatement("update file_body set processed=true, hash=? where _id=?");


//            int max = 10;
//            int i = 0;
            while (true)
            {
//                if (i++ > max) {
//                    log("PREMATURE EXIT, REACHED LIMIT!");
//                    return;
//                }
                try
                {
                    rs = getSingleFileToProcessStatement.executeQuery();
                    if (!rs.next())
                    {
                        log("REMOVING UNNEEDED FILES FROM DB");
                        markProcessedStatement=connection.prepareStatement("UPDATE file_body fb SET body=NULL WHERE _id IN (SELECT file.body FROM file WHERE file.body= fb._id)");
                        markProcessedStatement.execute();
                        log("FINISHED SUCCESSFULLY!");
                        return;
                    }

                    int id = rs.getInt(1);
                    byte[] bytes = rs.getBytes(2);
                    String originalFileName = rs.getString(3);

                    if (bytes == null)
                    {
                        log("SKIPPING SYSTEM " + id + " " + originalFileName);

                        markProcessedStatement.setString(1, null);
                        markProcessedStatement.setInt(2, id);
                        markProcessedStatement.execute();
                        continue;
                    }

                    byte[] digestBytes = MessageDigest.getInstance("SHA-256").digest(bytes);
                    String filename = new String(Hex.encodeHex(digestBytes));
                    String folder = filename.substring(0, 2) + "/" + filename.substring(2, 4);
                    String shortFilename = filename.substring(4);

                    new File(rootFSDir + folder).mkdirs();

                    File actualFile = new File(rootFSDir + folder + "/" + shortFilename);
                    if (actualFile.exists())
                    {
                        markProcessedStatement.setString(1, filename);
                        markProcessedStatement.setInt(2, id);
                        markProcessedStatement.execute();
                        continue;
                    }
                    FileOutputStream fos = new FileOutputStream(actualFile);
                    CopyUtils.copy(bytes, fos);
                    fos.close();
                    log("copied " + bytes.length + " bytes to " + actualFile.getAbsolutePath());

                    markProcessedStatement.setString(1, filename);
                    markProcessedStatement.setInt(2, id);
                    markProcessedStatement.execute();
                } finally
                {
                    if (rs != null)
                    {
                        rs.close();
                    }
                }
            }
        } catch (Exception e)
        {
            log("Could not finish.");
            e.printStackTrace();
        } finally
        {
            log("COLLISIONS:" + collisions);
            if (rs != null)
            {
                try
                {
                    rs.close();
                } catch (SQLException e)
                {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
            if (connection != null)
            {
                try
                {
                    connection.close();
                } catch (SQLException e)
                {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }


    }

    public void ensureFilesystemReady()
    {
        if (!rootFSDir.endsWith("/"))
        {
            rootFSDir = rootFSDir + "/";
        }
        rootFilesystemDir = new File(rootFSDir);
        if (!rootFilesystemDir.exists())
        {
            rootFilesystemDir.mkdirs();
        }
    }
}
