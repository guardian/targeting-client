package targeting.client

import com.amazonaws.services.s3._
import com.amazonaws.services.s3.model._
import org.apache.commons.io.IOUtils
import scala.util.Try

object S3 {
  def get(client: AmazonS3Client, bucket: String, path: String): S3Result = {
      Try(IOUtils.toByteArray(client.getObject(bucket, path).getObjectContent))
        .map(S3Success(_))
        .recover(convertAmazonExceptions)
        .get
  }

  private def convertAmazonExceptions: PartialFunction[Throwable, S3Result] = {
    case ex: AmazonS3Exception if ex.getErrorCode == "AccessDenied" => S3NotAuthorized(ex.getMessage)
    case ex: AmazonS3Exception if ex.getErrorCode == "NoSuchKey"    => S3NotFound(ex.getMessage)
    case ex: AmazonS3Exception                                      => S3UnknownException(ex.getMessage)
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

trait S3Result

case class S3Success(data: Array[Byte]) extends S3Result

case class S3NotFound(message: String) extends S3Result
case class S3NotAuthorized(message: String) extends S3Result
case class S3UnknownException(message: String) extends S3Result
