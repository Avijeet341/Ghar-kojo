package com.avi.gharkhojo.notifications

import com.google.auth.oauth2.GoogleCredentials
import java.io.InputStream

class AccessToken {
    val firebaseMessagingScope:String = "https://www.googleapis.com/auth/firebase.messaging"
    fun getAccessToken(): String?{
        try {
            var jsonString: String = "{\n" +
                    "  \"type\": \"service_account\",\n" +
                    "  \"project_id\": \"gharkhojo-61e80\",\n" +
                    "  \"private_key_id\": \"f0f47426762de032c617c563c3c4cef45651349d\",\n" +
                    "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDWEidCMOZXSQLy\\nyC+ES0BnKzcB9cYba9gYllg1/KPWeFRiRVty+DBSBrkB66b3FFdAxTn0v2XfWiH8\\nsqftBlvP7H4CB+T5tweOWEiXprtgPjLeingJ/b5J7oS+AwpuC4l2MMGSBMsZE9lS\\nCvytw1oYq2suPvQg3x/9tzqM9/6tYXXzeqvPBhrWmmQNN+JMeWiDlQLa3MQKwAvV\\nfJk5Bnd4yL0V/rk7lgzeeloj2OXMmPXj6njm3cQg8KfqRgEs7JVvev69wacc8cqW\\nF6c+QuUK26/L1cvh1+xTXya/phURoGsMobbthuyg4ML9FB18cA1DTRhKdODeVIgH\\nyxh2vC2nAgMBAAECggEAL/WIMxWWfUafsPm1X4umZZayb696zyjllHiWUZ4ErKdv\\nrEaCmurMlIvqBeqgEOnPNHUbK2B398aPJ7h0067hgd8nSdgv7dwaBsNzm/ogL4sv\\nxVi7VO2ypIiZ16IBw661ALQioc6OG9OtE+rfOiSyeiVpUUaMVhL27VYP+YYT2g67\\nByPrmB2qMOWkPgyIYJWIst9KepEwByiBPGrk33otkADWiaKdYF3jVcrKNNaAF3HB\\ndAS+oPGEE2pDr0iYJoeYcvcICmO7dvorYFrd7R+jwnMimfcEqtjycdDXGPokmcNp\\nF7vKxu+LlsEaQwRuZptfoCQLszR47ywFF/nUkq3sLQKBgQDa3lCav27K0NrVP+27\\naF8eoaPYhHJvBzbNdWgoox782DTXIe/q7j96WLjtmxBEm8Skr91/prU4G6zYDnP2\\nAb8EtgIv7vDhl1dRecrfcr9J+RYlLVHDIRyH6Ev2giMh+bUQcshzm2B3W6mu9cYY\\n8s4CYRdP/dNBF6+9HktuUao0kwKBgQD6Y3pPeuu9XphMggp+0T0hA8+l1laejTm2\\nYwG8IwT0Epd2kNHb7a5iRBcnyq/oMt0AgYpRBM3+g3D3/q/jYNCQRSZarbn6FkGs\\n7djPgHGEqbeTF1oZVimxu34ExHotlUyoDoEgDZ0OMaGpm4XAn/RjK05uOxIQjnDC\\ngha32V+DHQKBgDLxR+Wc5RbD4gt8o8LZGjqw+MfunAwHq9PKngHHZfEICdSBC6ys\\n442AlRqI2xJ9Bxol7PJHRSrYFjT7uDXsVh+slXm+rcZ0SVindnbn6Go5VdQ1ZjB6\\nxn9DrFqmE7NWwbQgY1O1OJDPfwFAJ2mrKhQXsStnMwKVlsv3/yqgS3ONAoGBALnq\\nn2jgpqYn1QIKFOzxRGh9QfOrYQm9Zzrr7N/bSNRJ8bkR8yFQLIm+cUsUjuTkz4CO\\nrztVS/dNddZOgB9fRb25+q6eDFeLqxxSRwhcIO4Wbr6DOl1Rwwe7Zej4UpgCIbcv\\nfeVk9lcRty4O0pOisX3pLAVhSxqMCWID4gaFGwxpAoGALUT65V+2GdXAHqxaH4wJ\\nBLW87E+7ov3OCtatBaywB8w3EPwZpYw2wflvLGk1r+c1F5bygqVBKehKwHQcDtgt\\nRAYlItwqrZu6gLvgI46Db5YOHOV6X+8FooWHsSZuOdMdqLcRQcwHTgWFb7R/2pwb\\nxYFwgB/QWG0Nc+eiqHJK1xU=\\n-----END PRIVATE KEY-----\\n\",\n" +
                    "  \"client_email\": \"firebase-adminsdk-gtjro@gharkhojo-61e80.iam.gserviceaccount.com\",\n" +
                    "  \"client_id\": \"108949995278573852324\",\n" +
                    "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                    "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                    "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                    "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-gtjro%40gharkhojo-61e80.iam.gserviceaccount.com\",\n" +
                    "  \"universe_domain\": \"googleapis.com\"\n" +
                    "}\n"

            var stream: InputStream = jsonString.byteInputStream()
            var googleCredential:GoogleCredentials = GoogleCredentials.fromStream(stream)
                .createScoped(listOf(firebaseMessagingScope))

            googleCredential.refresh()

            return googleCredential.accessToken.tokenValue
        }catch (e: Exception){
            println(e.message)
            return null
        }
    }
}