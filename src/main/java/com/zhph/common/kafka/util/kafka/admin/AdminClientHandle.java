package com.zhph.common.kafka.util.kafka.admin;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zhph.common.kafka.util.SerializeUtil;

public class AdminClientHandle {

	
	private Logger logger = LoggerFactory.getLogger(AdminClientHandle.class);
	private static AdminClient client;
	
	private AdminClientHandle() {
		InputStream in = null;
		Properties props = new Properties();
		String bootstrapServers;
		try {
			in = SerializeUtil.class.getClassLoader().getResourceAsStream("kafka-producer.properties");
			props.load(in);
			bootstrapServers = props.getProperty("bootstrap.servers");
			props.clear();
			props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers);
			client = AdminClient.create(props);
		} catch (Exception e) {
			logger.error("kafka配置文件加载出错！");
		} finally{
			try {
				if (in!=null)
					in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("kafka配置文件释放出错！");
			}
		}
	}
	public static AdminClient getClient() {
		return client;
	}
	
	/**
	 * 获取集群内的topic
	 * @return
	 * @throws Exception
	 */
	public Set<String> listAllTopics() throws Exception{
		ListTopicsOptions options = new ListTopicsOptions();
    	options.listInternal(true);
        ListTopicsResult topics = client.listTopics(options);
        Set<String> topicNames = topics.names().get();
		return topicNames;
	}
	
}
