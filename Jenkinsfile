pipeline {

    agent any

    environment {

        // Java 21
        JAVA_HOME = '/usr/lib/jvm/java-21-amazon-corretto'
        PATH = "${JAVA_HOME}/bin:${env.PATH}"

        // AWS
        AWS_REGION = 'ap-south-1'
        AWS_ACCOUNT_ID = '257212469434'

        // ECR
        ECR_REPOSITORY = 'java-app-project'
        ECR_URI = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}://{ECR_REPOSITORY}"

        // ECS
        ECS_CLUSTER = 'app-java-cluster'
        ECS_SERVICE = 'app-java-service'
        
        // CRITICAL: Set these two strings to match your exact AWS ECS Console settings
        ECS_TASK_FAMILY = 'java-app-project' 
        CONTAINER_NAME  = 'java-app-container' 

        // Docker image
        IMAGE_TAG = "${BUILD_NUMBER}"
    }

    stages {

        stage('Check Java and Maven') {
            steps {
                sh '''
                    echo "========== JAVA VERSION =========="
                    java -version

                    echo "========== JAVA HOME =========="
                    echo $JAVA_HOME

                    echo "========== MAVEN VERSION =========="
                    mvn -version
                '''
            }
        }

        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/Sanika11003/java-app-project.git'
            }
        }

        stage('Maven Build') {
            steps {
                sh '''
                    echo "========== MAVEN BUILD =========="

                    mvn clean package -DskipTests
                '''
            }
        }
        stage('Docker Build') {
            steps {
                sh """
                    echo "========== DOCKER BUILD =========="

                    # FIXED: Hardcoded your real AWS account details to guarantee zero variable formatting errors
                    docker build --no-cache -t ://amazonaws.com{IMAGE_TAG} .
                """
            }
        }


        stage('ECR Login') {
            steps {
                sh '''
                    echo "========== ECR LOGIN =========="

                    aws ecr get-login-password \
                    --region ${AWS_REGION} | \
                    docker login \
                    --username AWS \
                    --password-stdin \
                    ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com
                '''
            }
        }

        stage('Docker Tag') {
            steps {
                sh '''
                    echo "========== DOCKER TAG =========="

                    # FIXED: Tags the built image to ECR URI directly
                    docker tag \
                    ${ECR_URI}:${IMAGE_TAG} \
                    ${ECR_URI}:latest
                '''
            }
        }

        stage('Push Image to ECR') {
            steps {
                sh '''
                    echo "========== PUSH IMAGE TO ECR =========="

                    docker push ${ECR_URI}:${IMAGE_TAG}

                    docker push ${ECR_URI}:latest
                '''
            }
        }

        stage('Deploy to ECS') {
            steps {
                sh '''
                    echo "========== ECS DEPLOYMENT =========="

                    # FIXED: Downloads current task layout, injects the new image version, and updates service
                    aws ecs describe-task-definition --task-definition ${ECS_TASK_FAMILY} --region ${AWS_REGION} --query taskDefinition > task-def.json
                    
                    node -e "
                    const fs = require('fs');
                    const data = JSON.parse(fs.readFileSync('task-def.json'));
                    const cleaned = {
                        family: data.family,
                        containerDefinitions: data.containerDefinitions,
                        volumes: data.volumes,
                        networkMode: data.networkMode,
                        placementConstraints: data.placementConstraints,
                        requiresCompatibilities: data.requiresCompatibilities,
                        cpu: data.cpu,
                        memory: data.memory,
                        taskRoleArn: data.taskRoleArn,
                        executionRoleArn: data.executionRoleArn
                    };
                    const container = cleaned.containerDefinitions.find(c => c.name === '${CONTAINER_NAME}');
                    if (container) {
                        container.image = '${ECR_URI}:${IMAGE_TAG}';
                    }
                    fs.writeFileSync('new-task-def.json', JSON.stringify(cleaned, null, 2));
                    "
                    
                    aws ecs register-task-definition --cli-input-json file://new-task-def.json --region ${AWS_REGION} > registered-task.json

                    aws ecs update-service \
                    --cluster ${ECS_CLUSTER} \
                    --service ${ECS_SERVICE} \
                    --task-definition ${ECS_TASK_FAMILY} \
                    --force-new-deployment \
                    --region ${AWS_REGION}
                    
                    # Clean up temporary JSON files
                    rm -f task-def.json new-task-def.json registered-task.json
                '''
            }
        }
    }

    post {

        success {
            echo '======================================'
            echo ' CI/CD PIPELINE SUCCESSFUL'
            echo ' Java 21 Build Successful'
            echo ' Docker Image Built'
            echo ' Image Pushed to ECR'
            echo ' ECS Deployment Triggered'
            echo '======================================'
        }

        failure {
            echo '======================================'
            echo ' CI/CD PIPELINE FAILED'
            echo ' Check the console output'
            echo '======================================'
        }
    }
}
