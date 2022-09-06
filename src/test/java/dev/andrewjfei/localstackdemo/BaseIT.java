package dev.andrewjfei.localstackdemo;

import dev.andrewjfei.localstackdemo.util.S3Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest
@ComponentScan("dev.andrewjfei.localstackdemo")
public abstract class BaseIT {

    private static String LOCALSTACK_DOCKER_IMAGE = "localstack/localstack:latest";

    @Container
    public static LocalStackContainer localstackContainer = new LocalStackContainer(DockerImageName.parse(LOCALSTACK_DOCKER_IMAGE))
            .withServices(LocalStackContainer.Service.S3);

}
