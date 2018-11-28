package org.monster.common.util;

import org.monster.common.constant.CommonConfig;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {

    /// 로그 유틸
    public static void appendTextToFile(final String logFile, final String msg) {

//		if(Prebot.Broodwar.getFrameCount() % 37 != 1) {
//			return;
//		}
//		
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(logFile), true));
            bos.write(msg.getBytes());
            bos.flush();
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /// 로그 유틸
    public static void overwriteToFile(final String logFile, final String msg) {
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(logFile)));
            bos.write(msg.getBytes());
            bos.flush();
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /// 파일 유틸 - 텍스트 파일을 읽어들인다
    public static String readFile(final String filename) {
        BufferedInputStream bis;
        StringBuilder sb = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(new File(filename)));
            sb = new StringBuilder();

            while (bis.available() > 0) {
                sb.append((char) bis.read());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    /// 파일 유틸 - 경기 결과를 텍스트 파일로부터 읽어들인다
    public void readResults() {
        //String enemyName = Monster.Broodwar.enemy().getName();
        String enemyName = "enemey";
        enemyName = enemyName.replace(" ", "_");

        String enemyResultsFile = CommonConfig.FileConfig.READ_DIRECTORY + enemyName + ".txt";

        // int wins = 0;
        // int losses = 0;

        // FILE file;
        // errno_t err;
        //
        // if ((err = fopen_s(&file, enemyResultsFile.c_str(), "r")) != 0)
        // {
        // std::cout << "Could not open file: " << enemyResultsFile.c_str();
        // }
        // else
        // {
        // char line[4096]; /* or other suitable maximum line size */
        // while (fgets(line, sizeof line, file) != null) /* read a line */
        // {
        // //std::stringstream ss(line);
        // //ss >> wins;
        // //ss >> losses;
        // }
        //
        // fclose(file);
        // }
    }

    /// 파일 유틸 - 경기 결과를 텍스트 파일에 저장한다
    public void writeResults() {
        //String enemyName = Monster.Broodwar.enemy().getName();
        String enemyName = "enemey";
        enemyName = enemyName.replace(" ", "_");

        String enemyResultsFile = CommonConfig.FileConfig.WRITE_DIRECTORY + enemyName + ".txt";

        String ss = null;

        // int wins = 1;
        // int losses = 0;

        // ss << wins << " " << losses << "\n";

        overwriteToFile(enemyResultsFile, ss);
    }
}