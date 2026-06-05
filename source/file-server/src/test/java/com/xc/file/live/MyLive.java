package com.xc.file.live;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacpp.Loader;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author lingkang
 * Created by 2022/5/10
 */
@Slf4j
public class MyLive extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("录屏直播");
        // 初始化直播推流
        LiveTranscribe liveTranscribe = new LiveTranscribe();
        // 推流地址
        HBox plugFlowBox = new HBox(); // 创建一个水平箱子
        Label plugFlowLabel = new Label("推流地址："); // 创建一个标签
        plugFlowLabel.setPrefHeight(50);
        plugFlowLabel.setAlignment(Pos.CENTER_LEFT);
        TextField plugFlowField = new TextField(); // 创建一个单行输入框
        plugFlowField.setPrefSize(300, 50); // 设置单行输入框的推荐宽高
        plugFlowField.setEditable(true);  // 设置单行输入框能否编辑
        plugFlowField.setPromptText("推流地址");  // 设置单行输入框的提示语
        plugFlowField.setAlignment(Pos.CENTER_LEFT);  // 设置单行输入框的对齐方式
        plugFlowField.setPrefColumnCount(11);  // 设置单行输入框的推荐列数
        plugFlowField.setText("rtmp://127.0.0.1:1935/live/stream");
        plugFlowBox.getChildren().addAll(plugFlowLabel, plugFlowField);// 给水平箱子添加一个单行输入框
        plugFlowBox.setPadding(new Insets(10, 10, 10, 10));//设置按钮与上右下左边缘留出10px距离
        // 保存地址
        HBox saveBox = new HBox(); // 创建一个水平箱子
        Label saveLabel = new Label("保存地址："); // 创建一个标签
        saveLabel.setPrefHeight(50);
        saveLabel.setAlignment(Pos.CENTER_LEFT);
        TextField saveField = new TextField(); // 创建一个单行输入框
        saveField.setPrefSize(300, 50); // 设置单行输入框的推荐宽高
        saveField.setEditable(true);  // 设置单行输入框能否编辑
        saveField.setPromptText("保存地址");  // 设置单行输入框的提示语
        saveField.setAlignment(Pos.CENTER_LEFT);  // 设置单行输入框的对齐方式
        saveField.setPrefColumnCount(11);  // 设置单行输入框的推荐列数
        saveField.setText("D:\\temp\\test\\output.mp4");
        saveBox.getChildren().addAll(saveLabel, saveField);// 给水平箱子添加一个单行输入框
        saveBox.setPadding(new Insets(10, 10, 10, 10));//设置按钮与上右下左边缘留出10px距离
        // 推送文件
        HBox uploadBox = new HBox(); // 创建一个水平箱子
        Label uploadLabel = new Label("推送文件："); // 创建一个标签
        uploadLabel.setPrefHeight(50);
        uploadLabel.setAlignment(Pos.CENTER_LEFT);
        // 开始按钮
        Button uploadButton = new Button("上传文件");
        uploadButton.setPrefHeight(50);
        final File[] file = {null};
        uploadButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
            file[0] = fileChooser.showOpenDialog(stage);
        });
        uploadBox.getChildren().addAll(uploadLabel, uploadButton);// 给水平箱子添加一个单行输入框
        uploadBox.setPadding(new Insets(0, 10, 10, 10));//设置按钮与上右下左边缘留出10px距离
        // 开始按钮
        Button startButton = new Button("开始推送");
        startButton.setOnAction(event -> {
            String plugFlowUrl = plugFlowField.getText();
            liveTranscribe.setPlugFlowPath(plugFlowUrl);
            String localPath = saveField.getText();
            liveTranscribe.setLocalPath(localPath);
            if (file[0] != null) {
                liveTranscribe.setInputPath(file[0].getPath());
            }
            liveTranscribe.startTranscribe();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("info");
            alert.setHeaderText("开始推送");
            alert.setOnCloseRequest(event1 -> alert.hide());
            alert.showAndWait();
        });
        // 停止按钮
        Button stopButton = new Button("停止推送");
        stopButton.setOnAction(event -> {
            liveTranscribe.stopTranscribe();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("info");
            alert.setHeaderText("已经停止推送");
            alert.setOnCloseRequest(event1 -> alert.hide());
            alert.showAndWait();
        });
        HBox plugButtonBox = new HBox();    //设置水平根节点
        plugButtonBox.setPadding(new Insets(0, 10, 10, 10));//设置按钮与上右下左边缘留出10px距离
        plugButtonBox.getChildren().addAll(startButton, stopButton);//设置三个按钮水平呈现
        HBox.setMargin(stopButton, new Insets(0, 0, 0, 10));
        // 用于显示推送视频
        HBox imageBox = new HBox(); // 创建一个水平箱子
        ImageView imageVideo = new ImageView();
        imageVideo.setFitWidth(800);
        imageVideo.setFitHeight(600);
        liveTranscribe.setConfig(imageVideo);
        imageBox.getChildren().add(imageVideo);
        HBox.setMargin(imageVideo, new Insets(10, 20, 20, 20));
        // 页面布局
        VBox box = new VBox();
        box.getChildren().addAll(plugFlowBox, saveBox, uploadBox, plugButtonBox, imageBox);
        stage.setScene(new Scene(box));
        // 窗口
        stage.setWidth(860);
        stage.setHeight(820);
        stage.show();
        stage.setOnCloseRequest(event -> {// 退出时停止
            liveTranscribe.stopTranscribe();
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch(args);
//        plugFlow();
    }

    /**
     * m3u8转mp4格式
     */
    public static void plugFlow() {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        long startTime = System.currentTimeMillis();
        try {
            List<String> command = new ArrayList<>();
            //获取JavaCV中的ffmpeg本地库的调用路径
            String ffmpeg = Loader.load(org.bytedeco.ffmpeg.ffmpeg.class);
            command.add(ffmpeg);
            command.add("-i");
            command.add("D:\\output.mp4");
            command.add("-vcodec");
            command.add("libx264");
            command.add("-f");
            command.add("flv");
            command.add("rtmp://127.0.0.1:1935/live/stream");
            Process videoProcess = new ProcessBuilder(command).redirectErrorStream(true).start();
            executorService.execute(() -> {
                try (InputStream is = videoProcess.getErrorStream()) {
                    while (true) {
                        int ch = is.read();
                        if (ch != -1) {
                            System.out.print((char) ch);
                        } else {
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            executorService.execute(() -> {
                try (InputStream is = videoProcess.getInputStream()) {
                    while (true) {
                        int ch = is.read();
                        if (ch != -1) {
                            System.out.print((char) ch);
                        } else {
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            videoProcess.waitFor();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            long endTime = System.currentTimeMillis();
            log.info("用时:" + (int) ((endTime - startTime) / 1000) + "秒");
        }
    }
}


