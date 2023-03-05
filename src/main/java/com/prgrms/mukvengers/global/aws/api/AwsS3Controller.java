package com.prgrms.mukvengers.global.aws.api;

import java.io.IOException;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.prgrms.mukvengers.global.aws.service.AwsS3Service;
import com.prgrms.mukvengers.global.aws.util.AwsS3;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s3")
public class AwsS3Controller {

	private final AwsS3Service awsS3Service;

	@PostMapping("/resource")
	public AwsS3 upload(@RequestPart("file") MultipartFile multipartFile) throws IOException {
		return awsS3Service.upload(multipartFile, "profile");
	}

	@DeleteMapping("/resource")
	public void remove(AwsS3 awsS3) {
		awsS3Service.remove(awsS3);
	}
}