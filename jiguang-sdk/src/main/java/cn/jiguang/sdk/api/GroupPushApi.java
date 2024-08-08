package cn.jiguang.sdk.api;

import cn.jiguang.sdk.bean.push.GroupPushSendParam;
import cn.jiguang.sdk.bean.push.GroupPushSendResult;
import cn.jiguang.sdk.client.GroupPushClient;
import cn.jiguang.sdk.codec.ApiDecoder;
import cn.jiguang.sdk.codec.ApiEncoder;
import cn.jiguang.sdk.codec.ApiErrorDecoder;
import feign.Client;
import feign.Feign;
import feign.Logger;
import feign.auth.BasicAuthRequestInterceptor;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import lombok.NonNull;

public class GroupPushApi {

    private final GroupPushClient groupPushClient;

    protected GroupPushApi(@NonNull GroupPushClient groupPushClient) {
        this.groupPushClient = groupPushClient;
    }

    public GroupPushSendResult send(@NonNull GroupPushSendParam param) {
        return groupPushClient.send(param);
    }

    public static class Builder {

        private Client client = new OkHttpClient();
        private String host = "https://api.jpush.cn";
        private String groupKey;
        private String groupMasterSecret;
        private Logger.Level loggerLevel = Logger.Level.BASIC;

        public Builder setClient(@NonNull Client client) {
            this.client = client;
            return this;
        }

        public Builder setHost(@NonNull String host) {
            this.host = host;
            return this;
        }

        public Builder setGroupKey(@NonNull String groupKey) {
            this.groupKey = groupKey;
            return this;
        }

        public Builder setGroupMasterSecret(@NonNull String groupMasterSecret) {
            this.groupMasterSecret = groupMasterSecret;
            return this;
        }

        public Builder setLoggerLevel(@NonNull Logger.Level loggerLevel) {
            this.loggerLevel = loggerLevel;
            return this;
        }

        public GroupPushApi build() {
            GroupPushClient groupPushClient = Feign.builder()
                    .client(client)
                    .requestInterceptor(new BasicAuthRequestInterceptor("group-" + groupKey, groupMasterSecret))
                    .encoder(new ApiEncoder())
                    .decoder(new ApiDecoder())
                    .errorDecoder(new ApiErrorDecoder())
                    .logger(new Slf4jLogger())
                    .logLevel(loggerLevel)
                    .target(GroupPushClient.class, host);
            return new GroupPushApi(groupPushClient);
        }
    }

}
