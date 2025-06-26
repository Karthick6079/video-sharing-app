import { HttpClient, HttpErrorResponse } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { initial } from "lodash";
import { environment } from "../../../environments/environment";
import { UploadVideoResponse } from "../../dto/upload-video-response";
import { catchError, throwError } from "rxjs";
import { StatusResponse } from "../../dto/status-response";
import { AWSUploadException } from "../../exceptions/aws-upload.exception";

@Injectable({
    providedIn: "root"
})
export class UploadVideoService {

    private UPLOAD_URL: string = '/upload/multipart/';

    constructor(private http: HttpClient) { }


    initiateUpload(filename: string, fileExtension: string) {
        const baseUrl = this.getVideoBaseUrl();
        return this.http.post<{ key: string; uploadId: string }>(
            `${baseUrl}initiate?filename=${filename}&fileExtension=${fileExtension}`, {}
          )
    }


    getPresignedUrl(key: string, uploadId: string, partNumber: number) {
        return this.http.get<{url:string}>(this.getVideoBaseUrl() + "url",
            {
                params: { key, uploadId, partNumber }
            });
    }

    completeUpload(key: string, uploadId: string, parts: { partNumber: number, entityTag: string }[]) {
        return this.http.post<UploadVideoResponse>(this.getVideoBaseUrl() + "complete", {
            key,
            uploadId,
            parts
        });
    }

   

    getVideoBaseUrl(): string {
        return environment.SERVICE_NAME + '/video' + this.UPLOAD_URL;
    }

}