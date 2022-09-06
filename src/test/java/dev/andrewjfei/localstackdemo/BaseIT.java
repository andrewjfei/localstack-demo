package dev.andrewjfei.localstackdemo;

import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest
public abstract class BaseIT {

    private static String LOCALSTACK_DOCKER_IMAGE = "localstack/localstack:latest";

    @Container
    public static LocalStackContainer localstackContainer = new LocalStackContainer(DockerImageName.parse(LOCALSTACK_DOCKER_IMAGE))
            .withServices(LocalStackContainer.Service.S3);

}
