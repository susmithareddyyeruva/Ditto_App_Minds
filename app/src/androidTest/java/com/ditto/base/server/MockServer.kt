package com.ditto.base.server

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import java.io.InputStreamReader

class MockServer {
    class ResponseLoginSuccessDispatcher : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            println("Mock-- "+request.path)
            request.path?.let {
                if (it.contains("/version?v=")) {
                   return MockResponse().setResponseCode(200).setBody(MockResponseFileReader("version.json").content)
                }
                else if(it.contains("/s/ditto/dw/shop/v19_1/content/LandingContent")){
                    return MockResponse().setResponseCode(200).setBody("{\"_v\":\"19.1\",\"_type\":\"content\",\"id\":\"LandingContent\",\"name\":\"Landing Content\",\"c_body\":{\"customerCareEmail\":\"joannsupport@joann.com\",\"customerCareePhone\":\"(330) 735-6576\",\"customerCareeTiming\":\"Monday - Friday 9am - 6pm EST\",\"videoUrl\":\"https://www.youtube.com/watch?v=Jli6Sqm-2DU\",\"imageUrl\":\"https://development.dittopatterns.com/on/demandware.static/-/Library-Sites-LibrarydittoShared/default/dwbc424609/mask_group5.png\"}}")
                }
                else if(it.contains("/s/ditto/dw/shop/v19_1/customers/auth?")){
                    return MockResponse().setResponseCode(200).setBody(MockResponseFileReader("login_success_response.json").content)
                }
                else if(it.contains("/api/v1/patterns/Android/trial")){
                    return MockResponse().setResponseCode(200).setBody(MockResponseFileReader("patterns.json").content)
                }
                else if(it.contains("/on/demandware.store/Sites-ditto-Site/default/TraceAppMyLibrary-Shows")){
                    return MockResponse().setResponseCode(200).setBody(MockResponseFileReader("patterns1.json").content)
                }
                else{
                    return MockResponse().setResponseCode(400)
                }

            }
            return MockResponse().setResponseCode(200).setBody("")
        }
    }

    //  login error dispatcher
    class ResponseLoginErrorDispatcher : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            println("Mock-- "+request.path)
            request.path?.let {
                if (it.contains("/version?v=")) {
                    return MockResponse().setResponseCode(200).setBody("{\n" +
                            "\"response\": {\n" +
                            "\"version\": \"1.0\",\n" +
                            "\"critical_version\": \"0.14(01)\",\n" +
                            "\"force_version\": \"0.14(01)\",\n" +
                            "\"title\": \"REQUIRED APP UPDATE\",\n" +
                            "\"body\": \"This version of the app is no longer supported and requires an update to ensure the best possible DITTO experience\",\n" +
                            "\"confirm\": \"UPDATE\",\n" +
                            "\"confirm_link\": \"https://play.google.com/store/apps\",\n" +
                            "\"cancel\": \"SKIP\",\n" +
                            "\"version_update\": true,\n" +
                            "\"critical_update\": false,\n" +
                            "\"force_update\": false\n" +
                            "},\n" +
                            "\"errors\": {}\n" +
                            "}")
                }
                else if(it.contains("/s/ditto/dw/shop/v19_1/content/LandingContent")){
                    return MockResponse().setResponseCode(200).setBody("{\"_v\":\"19.1\",\"_type\":\"content\",\"id\":\"LandingContent\",\"name\":\"Landing Content\",\"c_body\":{\"customerCareEmail\":\"joannsupport@joann.com\",\"customerCareePhone\":\"(330) 735-6576\",\"customerCareeTiming\":\"Monday - Friday 9am - 6pm EST\",\"videoUrl\":\"https://www.youtube.com/watch?v=Jli6Sqm-2DU\",\"imageUrl\":\"https://development.dittopatterns.com/on/demandware.static/-/Library-Sites-LibrarydittoShared/default/dwbc424609/mask_group5.png\"}}")
                }
                else if(it.contains("/s/ditto/dw/shop/v19_1/customers/auth?")){
                    return MockResponse().setResponseCode(400)
                }
                else{
                    return MockResponse().setResponseCode(400)
                }
            }
            return MockResponse().setResponseCode(200).setBody("")
        }
    }

    //  error dispatcher
    class ErrorDispatcher : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse = MockResponse().setResponseCode(400)
    }


    class MockResponseFileReader(path: String) {

        val content: String

        init {
            val reader = InputStreamReader(this.javaClass.classLoader?.getResourceAsStream(path))
            content = reader.readText()
            reader.close()
        }
    }
}