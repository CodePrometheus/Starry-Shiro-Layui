package com.star.common.exception;

/**
 * 文件下载异常
 *
 * @Author: zzStar
 * @Date: 03-09-2021 18:42
 */
public class FileDownloadException extends StarryException {
    private static final long serialVersionUID = -4353976687870027960L;

    public FileDownloadException(String message) {
        super(message);
    }
}
