package com.gu.targeting.client

import scala.collection.JavaConversions._
import com.amazonaws.services.s3._
import com.amazonaws.services.s3.model._
import org.apache.commons.io.IOUtils
import scala.util.Try
import scala.collection.mutable.ListBuffer

object S3 {
  def get(client: AmazonS3Client, bucket: String, path: String): Array[Byte] = {
    try {
      IOUtils.toByteArray(client.getObject(bucket, path).getObjectContent)
    } catch {
      case ex: AmazonS3Exception if ex.getErrorCode == "AccessDenied" => throw AuthenticationException(ex.getMessage)
      case ex: AmazonS3Exception if ex.getErrorCode == "NoSuchKey"    => throw TargetingNotFoundException(ex.getMessage)
      case ex: AmazonS3Exception                                      => throw UnknownException(ex.getMessage)
    }
  }

  def list(client: AmazonS3Client, bucket: String, path: String): List[String] = {
    var paths: ListBuffer[String] = ListBuffer()

    try {
      var req = new ListObjectsV2Request()
        .withBucketName(bucket)
        .withPrefix(path)

      var isTruncated = false
      do {
        val result: ListObjectsV2Result = client.listObjectsV2(req)
        isTruncated = result.isTruncated

        result.getObjectSummaries().foreach { paths += _.getKey }

        req.setContinuationToken(result.getNextContinuationToken())
      } while (isTruncated)

      paths.toList
    } catch {
      case ex: AmazonS3Exception if ex.getErrorCode == "AccessDenied" => throw AuthenticationException(ex.getMessage)
      case ex: AmazonS3Exception if ex.getErrorCode == "NoSuchKey"    => throw TargetingNotFoundException(ex.getMessage)
      case ex: AmazonS3Exception                                      => throw UnknownException(ex.getMessage)
    }
  }

  def put(client: AmazonS3Client, bucket: String, path: String, text: String) = {
    try {
      client.putObject(bucket, path, text)
    } catch {
      case ex: AmazonS3Exception if ex.getErrorCode == "AccessDenied" => throw AuthenticationException(ex.getMessage)
      case ex: AmazonS3Exception if ex.getErrorCode == "NoSuchKey"    => throw TargetingNotFoundException(ex.getMessage)
      case ex: AmazonS3Exception                                      => throw UnknownException(ex.getMessage)
    }
  }
}
