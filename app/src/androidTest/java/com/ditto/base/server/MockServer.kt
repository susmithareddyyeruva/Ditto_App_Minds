package com.ditto.base.server

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import java.io.InputStreamReader

class MockServer {

    class ResponseLoginSuccessDispatcher : Dispatcher() {
        var applyFavoriteFilter = false
        override fun dispatch(request: RecordedRequest): MockResponse {
            println("Mock-- "+request.path)
            if(request.body.size > 0) {
                val result = request.body.readUtf8()
                applyFavoriteFilter = result.contains("Favorite")
            }
            request.path?.let {
                when {
                    it.contains("/version?v=") -> {
                        return MockResponse().setResponseCode(200).setBody(MockResponseFileReader("version.json").content)
                    }
                    it.contains("/s/ditto/dw/shop/v19_1/content/LandingContent") -> {
                        return MockResponse().setResponseCode(200).setBody("{\"_v\":\"19.1\",\"_type\":\"content\",\"id\":\"LandingContent\",\"name\":\"Landing Content\",\"c_body\":{\"customerCareEmail\":\"joannsupport@joann.com\",\"customerCareePhone\":\"(330) 735-6576\",\"customerCareeTiming\":\"Monday - Friday 9am - 6pm EST\",\"videoUrl\":\"https://www.youtube.com/watch?v=Jli6Sqm-2DU\",\"imageUrl\":\"https://development.dittopatterns.com/on/demandware.static/-/Library-Sites-LibrarydittoShared/default/dwbc424609/mask_group5.png\"}}")
                    }
                    it.contains("/s/ditto/dw/shop/v19_1/customers/auth?") -> {
                        return MockResponse().setResponseCode(200).setBody(MockResponseFileReader("login_success_response.json").content)
                    }
                    it.contains("/api/v1/patterns/Android/trial") -> {
                        return MockResponse().setResponseCode(200).setBody(MockResponseFileReader("trial_patterns.json").content)
                    }
                    it.contains("/on/demandware.store/Sites-ditto-Site/default/TraceAppMyLibrary-Shows") && !applyFavoriteFilter -> {
                        return MockResponse().setResponseCode(200).setBody(MockResponseFileReader("all_patterns.json").content)
                    }
                    it.contains("/on/demandware.store/Sites-ditto-Site/default/TraceAppMyLibrary-Shows") && applyFavoriteFilter -> {
                        return MockResponse().setResponseCode(200).setBody(MockResponseFileReader("favorite_folder.json").content)
                    }
                    it.contains("/s/-/dw/data/v19_1/customer_lists/ditto/customers/00001001") -> {
                        return MockResponse().setResponseCode(200).setBody(MockResponseFileReader("delete_account_response.json").content)
                    }
                    it.contains("/s/ditto/dw/shop/v19_1/content/tutorial?&client_id=59eb4dd9-026d-4bac-adb5-2e5848d91263") -> {
                        return MockResponse().setResponseCode(200).setBody(MockResponseFileReader("tutorial_response.json").content)
                    }
                    it.contains("/on/demandware.store/Sites-ditto-Site/default/TraceAppLibraryFolder-Modify?method=getFolders") -> {
                        return MockResponse().setResponseCode(200).setBody(MockResponseFileReader("myFolder.json").content)
                    }
                    it.contains("/api/v1/patterns/a0b4c687dfdb409eac8ef7df82e8383f?os=Android&mannequinId=c45c1e4c15cf42f2a24b3e2f496bcf14")->{
                        return MockResponse().setResponseCode(200).setBody(MockResponseFileReader("pattern_detail.json").content)
                    }
                    it.contains("/s/ditto/dw/shop/v19_1/custom_objects/traceWorkSpace/abcgkk33VRNlDOEkKNLQJWCE7y_00006817_a0b4c687dfdb409eac8ef7df82e8383f_c45c1e4c15cf42f2a24b3e2f496bcf14?&client_id=59eb4dd9-026d-4bac-adb5-2e5848d91263")->{
                        return MockResponse().setResponseCode(200).setBody(MockResponseFileReader("workspace.json").content)
                    }
                    it.contains("/s/-/dw/data/v19_1/custom_objects/traceWorkSpace/abG9UDG0XDatXqfy47uY3T1pRN_00006817_a0b4c687dfdb409eac8ef7df82e8383f_c45c1e4c15cf42f2a24b3e2f496bcf14?method=PATCH&client_id=59eb4dd9-026d-4bac-adb5-2e5848d91263&site_id=ditto")->{
                        return MockResponse().setResponseCode(200).setBody("")
                    }
                    it.contains("/s/ditto/dw/shop/v19_1/custom_objects/traceWorkSpace/abG9UDG0XDatXqfy47uY3T1pRN_00006817_a0b4c687dfdb409eac8ef7df82e8383f_c45c1e4c15cf42f2a24b3e2f496bcf14?&client_id=59eb4dd9-026d-4bac-adb5-2e5848d91263")->{
                        return MockResponse().setResponseCode(200)
                    }
                    else -> {
                        return MockResponse().setResponseCode(400)
                    }
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