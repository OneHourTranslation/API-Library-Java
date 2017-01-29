package com.oht;

import com.google.gson.*;
import com.oht.entities.*;
import org.apache.http.client.methods.*;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.*;
import java.net.URLEncoder;
import java.util.*;

public class OHTAPI {

    //<editor-fold desc="CONSTRUCTOR / PROPERTIES"

    private static String baseUrl = "http://www.onehourtranslation.com/api/2";
    private static String baseUrl_sandbox = "http://sandbox4.onehourtranslation.com/api/2";

    private String secretKey = "";
    private String publicKey = "";
    private boolean useSandbox = false;

    private static JsonParser parser = new JsonParser();

    /**
     * Constructs OHT API instance
     *
     * @param secretKey secret API key
     * @param publicKey public API key
     */
    public OHTAPI(String secretKey, String publicKey) {
        this(secretKey, publicKey, false);
    }

    /**
     * Constructs OHT API instance
     *
     * @param secretKey  secret API key
     * @param publicKey  public API key
     * @param useSandbox use sandbox environment
     */
    public OHTAPI(String secretKey, String publicKey, boolean useSandbox) {
        this.secretKey = secretKey;
        this.publicKey = publicKey;
        this.useSandbox = useSandbox;
    }

    //</editor-fold>

    //<editor-fold desc="ACCOUNTS">

    /**
     * Fetch basic account details and credits balance
     *
     * @return {@link AccountDetails AccountDetails} object
     * @throws OHTException
     */
    public AccountDetails getAccountDetails() throws OHTException {
        JsonElement account = request("/account").get();
        AccountDetails result = new AccountDetails(account.getAsJsonObject());
        return result;
    }

    //</editor-fold>

    //<editor-fold desc="RESOURCES">

    /**
     * Create a new file entity on One Hour Translation
     *
     * @param fileName       replacement for original file's name on One Hour Translation
     * @param fileMime       replacement for default mime value for the file
     * @param uploadFilePath full path to file to upload
     * @param fileContent    content of the new file, works only with {@code fileName} not empty.
     *                       If used, actual upload is skipped
     * @return resource UUID
     * @throws OHTException
     */
    public String uploadFileResource(String fileName, String fileMime, String uploadFilePath, String fileContent) throws OHTException {
        Request request
            = request("/resources/file")
                .param("file_name", fileName)
                .param("file_mime", fileMime)
                .param("file_content", fileContent);

        if (null != uploadFilePath)
            request.param("upload", new File(uploadFilePath));


        JsonArray array = request.post().getAsJsonArray();

        return array.get(0).getAsString();
    }

    /**
     * Create a new text resource on One Hour Translation
     *
     * @param text       Desired text to upload.

     * @return resource UUID
     * @throws OHTException
     */
    public String uploadTextResource(String text) throws OHTException {
        Request request
                = request("/resources/text")
                .param("text", text);

        JsonArray array = request.post().getAsJsonArray();

        return array.get(0).getAsString();
    }

    /**
     * Provides information regarding a specific resource
     *
     * @param resourceUuid UUID of resource
     * @param projectId    (optional) project ID, needed when requesting a resource that was uploaded
     *                     by another user - e.g. as a project’s
     * @param fetch        (optional) possible values: null - do not fetch content; base64 - fetch content, base64 encoded
     * @return {@link Resource Resource} object
     * @throws OHTException
     */
    public Resource getResource(String resourceUuid, Integer projectId, String fetch) throws OHTException {
        JsonElement json = request("/resources/" + resourceUuid).param("project_id", projectId).param("fetch", fetch).get();
        return new Resource(json.getAsJsonObject());
    }

    /**
     * Downloads resource by specified Resource UUID
     *
     * @param resourceUuid UUID of resource
     * @param projectId    (optional) project ID, needed when requesting a resource that was uploaded
     *                     by another user - e.g. as a project’s
     * @param fileToSave   full path to file to save
     * @throws OHTException
     */
    public void downloadResource(String resourceUuid, Integer projectId, String fileToSave) throws OHTException {
        request("/resources/" + resourceUuid + "/download")
                .param("project_id", projectId)
                .param("download", new File(fileToSave))
                .get();
    }

    //</editor-fold>

    //<editor-fold desc="TOOLS">

    /**
     * Order summary
     *
     * @param resources    array of Resource UUIDs
     * @param wordCount    word count
     * @param sourceLang   Source Language
     * @param targetLang   Target Language
     * @param service      translation | proofreading | transproof | transcription (defaults to translation)
     * @param expertise    use {@link #getSupportedExpertises(String, String) getSupportedExpertises} method
     *                     to get supported expertises for selected source and target language
     * @param proofreading (optional, defaults to {@code null}), "0" or "1"
     * @param currency     (optional, defaults to {@code null}), "USD" or "EUR"
     * @return {@link Quote Quote} object
     */
    public Quote getQuote
            (String[] resources,
            int wordCount,
            String sourceLang,
            String targetLang,
            String service,
            String expertise,
            String proofreading,
            String currency) throws OHTException {

        Request request
            = request("/tools/quote")
                .param("resources", resources)
                .param("wordcount", wordCount)
                .param("source_language", sourceLang)
                .param("target_language", targetLang)
                .param("service", service)
                .param("expertise", expertise)
                .param("proofreading", proofreading)
                .param("currency", currency);

        JsonElement json = request.get();

        return new Quote(json.getAsJsonObject());
    }

    /**
     * Returns total word count of provided resources
     *
     * @param resources array of Resource UUIDs
     * @return {@link WordCount WordCount} object
     */
    public WordCount getWordCount(String[] resources) throws OHTException {
        JsonElement json = request("/tools/wordcount").param("resources", resources).get();

        return new WordCount(json.getAsJsonObject());
    }

    //</editor-fold>

    //<editor-fold desc="PROJECT">

    /**
     * Creates translation project
     *
     * @param sourceLanguage source language
     * @param targetLanguage target language
     * @param resources      array of Resource UUIDs
     * @param wordCount      (optional) if empty use automatic counting
     * @param notes          (optional) text note that will be shown to translator regarding the newly project
     * @param expertise      (optional)
     * @param callbackUrl    (optional)
     * @param name           (optional) name your project. If empty, your project will be named automatically.
     * @param reference_resources (optional) array of reference resource UUIDs
     * @param custom         (optional) array of custom fields
     * @return {@link Project Project} object
     */
    public Project createTranslationProject
            (String sourceLanguage,
             String targetLanguage,
             String[] resources,
             Integer wordCount,
             String notes,
             String expertise,
             String callbackUrl,
             String name,
             String[] reference_resources,
             String[] custom) throws OHTException {

        Request request
            = request("/projects/translation")
                .param("source_language", sourceLanguage)
                .param("target_language", targetLanguage)
                .param("sources", resources)
                .param("wordcount", wordCount)
                .param("notes", notes)
                .param("callback_url", callbackUrl)
                .param("name", name);

        if (expertise != null && !expertise.equals("")){
            request.param("expertise", expertise);
        }

        if(reference_resources != null){
            request.param("reference_resources", reference_resources);
        }

        if(custom != null){
            for(int i = 0; i < custom.length; i++){
                request.param("custom"+String.valueOf(i), custom[i]);
            }
        }

        JsonElement json = request.post();

        return new Project(json.getAsJsonObject());
    }

    /**
     * Creates proofreading project from one - source language
     *
     * @param sourceLanguage source language
     * @param sources        array of Resource UUIDs
     * @param wordCount      (optional) if empty use automatic counting
     * @param notes          (optional) text note that will be shown to translator regarding the newly project
     * @param expertise      (optional)
     * @param callbackUrl    (optional)
     * @param name           (optional) name your project. If empty, your project will be named automatically.
     * @param reference_resources (optional) array of reference resource UUIDs
     * @param custom        (optional) array of custom fields
     * @return {@link Project Project} object
     */
    public Project createProofreadingProject
            (String sourceLanguage,
             String[] sources,
             Integer wordCount,
             String notes,
             String expertise,
             String callbackUrl,
             String name,
             String[] reference_resources,
             String[] custom) throws OHTException {

        Request request
            = request("/projects/proof-general")
                .param("source_language", sourceLanguage)
                .param("sources", sources)
                .param("wordcount", wordCount)
                .param("notes", notes)
                .param("expertise", expertise)
                .param("callback_url", callbackUrl)
                .param("name", name);

        if(custom != null){
            for(int i = 0; i < custom.length; i++){
                request.param("custom"+String.valueOf(i), custom[i]);
            }
        }

        if(reference_resources != null){
            request.param("reference_resources", reference_resources);
        }

        JsonElement json = request.post();

        return new Project(json.getAsJsonObject());
    }

    /**
     * Creates proofreading project (source and target languages)
     *
     * @param sourceLanguage source language
     * @param targetLanguage target language
     * @param sources        array of Resource UUIDs
     * @param translations   array of Resource UUIDs
     * @param wordCount      (optional) if empty use automatic counting
     * @param notes          (optional) text note that will be shown to translator regarding the newly project
     * @param callbackUrl    (optional)
     * @param name           (optional) name your project. If empty, your project will be named automatically.
     * @param reference_resources (optional) array of reference resource UUIDs
     * @param custom        (optional) array of custom fields
     * @return {@link Project Project} object
     */
    public Project createProofTranslatedProject
            (String sourceLanguage,
             String targetLanguage,
             String[] sources,
             String[] translations,
             Integer wordCount,
             String notes,
             String expertise,
             String callbackUrl,
             String name,
             String[] reference_resources,
             String[] custom) throws OHTException {

        Request request
            = request("/projects/proof-translated")
                .param("source_language", sourceLanguage)
                .param("target_language", targetLanguage)
                .param("sources", sources)
                .param("translations", translations)
                .param("wordcount", wordCount)
                .param("notes", notes)
                .param("callback_url", callbackUrl)
                .param("name", name);

        if (expertise != null && !expertise.equals("")){
            request.param("expertise", expertise);
        }

        if(custom != null){
            for(int i = 0; i < custom.length; i++){
                request.param("custom"+String.valueOf(i), custom[i]);
            }
        }

        if(reference_resources != null){
            request.param("reference_resources", reference_resources);
        }

        JsonElement json = request.post();
        return new Project(json.getAsJsonObject());
    }

    /**
     * Creates a transcription project
     *
     * @param sourceLanguage source language
     * @param sources        array of Resource UUIDs
     * @param length         (optional) number of seconds, if empty use automatic counting
     * @param notes          (optional) text note that will be shown to translator regarding the newly project
     * @param callbackUrl    (optional)
     * @param name           (optional) name your project. If empty, your project will be named automatically.
     * @param custom        (optional) array of custom fields
     * @return {@link Project Project} object
     */
    public Project createTranscriptionProject
            (String sourceLanguage,
             String[] sources,
             Integer length,
             String notes,
             String expertise,
             String callbackUrl,
             String name,
             String[] reference_resources,
             String[] custom) throws OHTException {

        Request request
            = request("/projects/transcription")
                .param("source_language", sourceLanguage)
                .param("sources", sources)
                .param("length", length)
                .param("notes", notes)
                .param("expertise", expertise)
                .param("callback_url", callbackUrl)
                .param("name", name);

        if(custom != null){
            for(int i = 0; i < custom.length; i++){
                request.param("custom"+String.valueOf(i), custom[i]);
            }
        }

        if(reference_resources != null){
            request.param("reference_resources", reference_resources);
        }

        JsonElement json = request.post();
        return new Project(json.getAsJsonObject());
    }

    /**
     * Creates translation plus editing project
     *
     * @param sourceLanguage source language
     * @param targetLanguage target language
     * @param resources      array of Resource UUIDs
     * @param wordCount      (optional) if empty use automatic counting
     * @param notes          (optional) text note that will be shown to translator regarding the newly project
     * @param expertise      (optional)
     * @param callbackUrl    (optional)
     * @param name           (optional) name your project. If empty, your project will be named automatically.
     * @param reference_resources (optional) array of reference resource UUIDs
     * @param custom         (optional) array of custom fields
     * @return {@link Project Project} object
     */
    public Project createTranslationPlusEditingProject
    (String sourceLanguage,
     String targetLanguage,
     String[] resources,
     Integer wordCount,
     String notes,
     String expertise,
     String callbackUrl,
     String name,
     String[] reference_resources,
     String[] custom) throws OHTException {

        Request request
                = request("/projects/transproof")
                .param("source_language", sourceLanguage)
                .param("target_language", targetLanguage)
                .param("sources", resources)
                .param("wordcount", wordCount)
                .param("notes", notes)
                .param("callback_url", callbackUrl)
                .param("name", name);

        if (expertise != null && !expertise.equals("")){
            request.param("expertise", expertise);
        }

        if(reference_resources != null){
            request.param("reference_resources", reference_resources);
        }

        if(custom != null){
            for(int i = 0; i < custom.length; i++){
                request.param("custom"+String.valueOf(i), custom[i]);
            }
        }

        JsonElement json = request.post();

        return new Project(json.getAsJsonObject());
    }

    /**
     * Gets a detailed specification of the project
     *
     * @param projectId project id
     * @return {@link ProjectDetails ProjectDetails} object
     * @throws OHTException
     */
    public ProjectDetails getProjectDetails(int projectId) throws OHTException {
        JsonElement json = request("/projects/" + Integer.toString(projectId)).get();
        return new ProjectDetails(json.getAsJsonObject());
    }

    /**
     * Cancels a project before work begins
     *
     * @param projectId project id
     * @throws OHTException
     */
    public void cancelProject(int projectId) throws OHTException {
        request("/projects/" + Integer.toString(projectId)).delete();
    }

    /**
     * Receive comments posted on the project page
     *
     * @param projectId project id
     * @return collection of {@link Comment Comment} objects
     * @throws OHTException
     */
    public Collection<Comment> getProjectComments(int projectId) throws OHTException {
        JsonElement json = request(String.format("/projects/%d/comments", projectId)).get();

        List<Comment> results = new ArrayList<Comment>();
        JsonArray array = json.getAsJsonArray();
        for (JsonElement e : array)
            results.add(new Comment(e.getAsJsonObject()));

        Collections.sort(results);

        return results;
    }

    /**
     * Post a new comment to the project page
     *
     * @param projectId project id
     * @param content   text
     * @throws OHTException
     */
    public void postProjectComment(int projectId, String content) throws OHTException {
        request(String.format("/projects/%d/comments", projectId))
                .param("content", content)
                .post();
    }

    /**
     * Get the rating for the quality of the translation and service
     *
     * @param projectId project id
     * @return collection of {@link Rating Rating} objects
     * @throws OHTException
     */
    public Collection<Rating> retrieveProjectRatings(int projectId) throws OHTException {
        JsonElement json = request(String.format("/projects/%d/rating", projectId)).get();

        List<Rating> results = new ArrayList<Rating>();
        JsonArray array = json.getAsJsonArray();
        for (JsonElement e : array)
            results.add(new Rating(e.getAsJsonObject()));

        Collections.sort(results);

        return results;
    }

    /**
     * Posts a rating for the quality of the translation and service
     *
     * @param projectId project id
     * @param type      "customer" or "service"
     * @param rate      rating of project (1 - being the lowest; 10 - being the highest)
     * @param remarks   remark left with the rating
     * @param publish   Allow OneHourTranslation to publish rating on Yotpo. 0 - Don't publish, 1 - Publish
     * @param additionalRating  (optional) Extra Service / translation rating values provided via checkboxes.
                                Service rating must provide fields (all are boolean 0 | 1 values):
                                service_was_on_time - Service on time, or took too long
                                service_support_helpful - The support was helpful
                                service_good_quality - The service was with good quality?
                                service_trans_responded - Slowly / Quickly
                                service_would_recommend - The site would be recommended

                                Customer rating must provide fields (all are boolean 0 | 1 values):
                                trans_is_good - Good translation quality
                                trans_bad_formatting - Bad formatting
                                trans_misunderstood_source - source misunderstood/misrepresent
                                trans_spell_tps_grmr_mistakes - Spelling/Typos/Grammer mistakes
                                trans_text_miss - Missing text / Partly untranslated
                                trans_not_followed_instrctns - Translator didn't follow instructions
                                trans_inconsistent - Translation is inconsistent
                                trans_bad_written - Translation written badly, or too literal

                                Example of passing categories parameters:
                                rating_type - customer:
                                {categories[trans_inconsistent]=1, categories[trans_text_miss]=1}
                                rating_type - service:
                                {categories[service_good_quality]=0, categories[service_was_on_time]=1}
     * @throws OHTException
     */
    public void postProjectRating(int projectId, String type, int rate, String remarks, int publish, HashMap<String, Integer> additionalRating) throws OHTException {
        Request request = request(String.format("/projects/%d/rating", projectId))
                .param("type", type)
                .param("rate", rate)
                .param("remarks", remarks)
                .param("publish", publish);

        for(Map.Entry<String, Integer> e : additionalRating.entrySet()){
            request.param(e.getKey(), e.getValue());
        }
        request.post();
    }

    // </editor-fold>

    //<editor-fold desc="MACHINE TRANSLATION">

    /**
     * Translate via machine translation
     *
     * @param sourceLanguage source language
     * @param targetLanguage target language
     * @param sourceContent  text for translation
     * @return translated text
     * @throws OHTException
     */
    public String machineTranslation(String sourceLanguage, String targetLanguage, String sourceContent) throws OHTException {
        JsonElement json
                = request("/mt/translate/text")
                .param("source_language", sourceLanguage)
                .param("target_language", targetLanguage)
                .param("source_content", sourceContent)
                .post();

        return json.getAsJsonObject().get("TranslatedText").getAsString();
    }

    /**
     * Detects language via machine translation
     *
     * @param sourceContent text for translation
     * @return detected language
     * @throws OHTException
     */
    public String detectLanguage(String sourceContent) throws OHTException {
        JsonElement json = request("/mt/detect/text").param("source_content", sourceContent).post();

        return json.getAsJsonObject().get("language").getAsString();
    }

    //</editor-fold>

    //<editor-fold desc="DISCOVER">

    /**
     * Gets supported languages
     *
     * @return collection of {@link Language Language} objects
     * @throws OHTException
     */
    public Collection<Language> getSupportedLanguages() throws OHTException {
        JsonElement json = request("/discover/languages").get();

        List<Language> results = new ArrayList<Language>();
        JsonArray array = json.getAsJsonArray();
        for (JsonElement e : array)
            results.add(new Language(e.getAsJsonObject()));

        return results;
    }

    /**
     * Gets supported language pairs
     *
     * @return collection of the {@link LanguagePair LanguagePair} objects
     * @throws OHTException
     */
    public Collection<LanguagePair> getSupportedLanguagePairs() throws OHTException {
        JsonElement json = request("/discover/language_pairs").get();

        List<LanguagePair> results = new ArrayList<LanguagePair>();
        JsonArray array = json.getAsJsonArray();
        for (JsonElement e : array)
            results.add(new LanguagePair(e.getAsJsonObject()));

        return results;
    }

    /**
     * Gets supported expertises
     *
     * @param sourceLanguage source language
     * @param targetLanguage target language
     * @return collection of the {@link Expertise Expertise} objects
     * @throws OHTException
     */
    public Collection<Expertise> getSupportedExpertises(String sourceLanguage, String targetLanguage) throws OHTException {
        JsonElement json
            = request("/discover/expertise")
                .param("source_language", sourceLanguage)
                .param("target_language", targetLanguage)
                .get();

        List<Expertise> results = new ArrayList<Expertise>();
        JsonArray array = json.getAsJsonArray();
        for (JsonElement e : array)
            results.add(new Expertise(e.getAsJsonObject()));

        return results;
    }

    /**
     * Receive tags added to the project
     *
     * @param projectId project id
     * @return Map of tag id and string {@link Tags Comment} objects
     * @throws OHTException
     */
    public Tags getProjectTags(int projectId) throws OHTException {
        JsonElement json = request(String.format("/project/%d/tag", projectId)).get();

        return new Tags(json.getAsJsonObject());
    }

    /**
     * Delete project tag by tag id
     *
     * @param projectId project id
     * @param tagId tag id
     * @throws OHTException
     */
    public void deleteProjectTag(int projectId, int tagId) throws OHTException {
        request(String.format("/project/%d/tag/%d", projectId, tagId)).delete();
    }

    /**
     * Add a new tag to project
     *
     * @param projectId project id
     * @param tag tag string
     * @throws OHTException
     */
    public void addProjectTag(int projectId, String tag) throws OHTException {
        request(String.format("/project/%d/tag/", projectId)).param("tag_name", tag).post();
    }

    //</editor-fold>

    //<editor-fold desc="REQUEST INTERNALS"

    private Request request(String requestUrl) {
        Request result = new Request(requestUrl);
        result.param("secret_key", secretKey);
        result.param("public_key", publicKey);
        return result;
    }

    // singleton instance of HttpClient
    static CloseableHttpClient client = null;
    private CloseableHttpClient getClient() {
        if (null == client)
            client = HttpClients.createDefault();

        return client;
    }

    private class Request {
        private String requestUrl = null;
        private SortedMap<String, Object> params = null; // named request parameters
        private File file = null; // file to upload or download

        private Request(String requestUrl) {
            this.requestUrl = requestUrl;
            this.params = new TreeMap<String, Object>();
        }

        public Request param(String name, String value) {
            this.params.put(name, value);
            return this;
        }

        public String joinStrings(String[] array, String delimiter) {
            StringBuilder result = new StringBuilder();
            for (int i = 0, il = array.length; i < il; i++) {
                if (i > 0) result.append(delimiter);
                result.append(array[i]);
            }

            return result.toString();
        }

        public Request param(String name, String[] value) {
            String comaSeparated = joinStrings(value, ",");
            this.params.put(name, comaSeparated);
            return this;
        }

        public Request param(String name, Integer value) {
            this.params.put(name, value);
            return this;
        }

        public Request param(String name, File value) {
            file = value;
            return this;
        }

        public JsonElement post() throws OHTException {
            return execute("POST");
        }

        public JsonElement get() throws OHTException {
            return execute("GET");
        }

        public JsonElement delete() throws OHTException {
            return execute("DELETE");
        }

        private JsonElement execute(String method) throws OHTException {
            CloseableHttpClient httpClient = null;
            HttpRequestBase request = null;
            CloseableHttpResponse response = null;
            FileOutputStream outputStream = null;
            JsonElement result = null;

            try {
                httpClient = getClient();

                StringBuilder urlString = new StringBuilder(useSandbox ? baseUrl_sandbox : baseUrl);
                urlString.append(requestUrl);

                ArrayList<String> stringParams = new ArrayList<String>();
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    if (null == value)
                        continue;

                    stringParams.add(String.format("%s=%s", key, URLEncoder.encode(value.toString(), "UTF-8")));
                }

                if (!stringParams.isEmpty()) {
                    urlString.append("?");
                    urlString.append(joinStrings(stringParams.toArray(new String[]{}), "&"));
                }

                if (method.equalsIgnoreCase("GET"))
                    request = new HttpGet(urlString.toString());
                else if (method.equalsIgnoreCase("POST"))
                    request = new HttpPost(urlString.toString());
                else if (method.equalsIgnoreCase("DELETE"))
                    request = new HttpDelete(urlString.toString());

                if (null != this.file) {

                    if (method.equalsIgnoreCase("GET")) // file download process
                    {
                        response = httpClient.execute(request);
                        InputStream inputStream = response.getEntity().getContent();
                        outputStream = new FileOutputStream(file, false);

                        int read = 0;
                        byte[] bytes = new byte[1024];

                        while ((read = inputStream.read(bytes)) != -1) {
                            outputStream.write(bytes, 0, read);
                        }

                        return null;
                    } else if (method.equalsIgnoreCase("POST")) // file upload process)
                    {
                        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                        builder.addBinaryBody("upload", this.file);
                        ((HttpPost) request).setEntity(builder.build());
                    } else {
                        // unreal situation
                        throw new OHTException(-1, "internal error", null);
                    }
                }

                response = httpClient.execute(request);

                JsonElement element = parser.parse(new InputStreamReader(response.getEntity().getContent()));
                if (!element.isJsonObject())
                    throw new OHTException(-1, "OneHourTranslation response was malformed.", null);

                JsonObject status = element.getAsJsonObject().get("status").getAsJsonObject();
                JsonArray errors = element.getAsJsonObject().get("errors").getAsJsonArray();

                int statusCode = status.get("code").getAsInt();
                String statusMessage = status.get("msg").getAsString();

                if (0 != statusCode) {
                    throw new OHTException(statusCode, statusMessage, errors);
                }

                result = element.getAsJsonObject().get("results");
            } catch (OHTException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new OHTException(ex);
            } finally {

                // close response
                if (null != response) {
                    try {
                        response.close();
                    } catch (IOException ex) {
                    }
                }

                // close file output stream
                if (null != outputStream) {
                    try {
                        outputStream.close();
                    } catch (IOException ex) {
                    }
                }
            }

            return result;
        }
    }

    //</editor-fold>
}
