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
        // FIXED: Removed the incorrect '://' and added the missing '$' before ECR_REPOSITORY
        ECR_REGISTRY   = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"
        ECR_URI        = "${ECR_REGISTRY}/${ECR_REPOSITORY}"

        // ECS
        ECS_CLUSTER = 'app-java-cluster'
        ECS_SERVICE = 'app-java-service'
        
        // CRITICAL: Set these two strings to match your exact AWS ECS Console settings
        ECS_TASK_FAMILY = 'app-java-task' 
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

                    # FIXED: Formatted tag accurately using double-quotes and pre-defined variables
                    docker build --no-cache -t ${ECR_URI}:${IMAGE_TAG} .
                """
            }
        }

        stage('ECR Login') {
            steps {
                sh """
                    echo "========== ECR LOGIN =========="

                    # FIXED: Updated string to cleanly evaluate environment tags
                    aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REGISTRY}
                """
            }
        }

        stage('Docker Tag') {
            steps {
                sh """
                    echo "========== DOCKER TAG =========="

                    # FIXED: Changed shell environment to double-quotes so variable injection parses correctly
                    docker tag ${ECR_URI}:${IMAGE_TAG} ${ECR_URI}:latest
                """
            }
        }

        stage('Push Image to ECR') {
            steps {
                sh """
                    echo "========== PUSH IMAGE TO ECR =========="

                    # FIXED: Changed shell environment to double-quotes so variable injection parses correctly
                    docker push ${ECR_URI}:${IMAGE_TAG}
                    docker push ${ECR_URI}:latest
                """
            }
        }

             stage('Deploy to ECS') {
            steps {
                sh """
                    echo "========== ECS DEPLOYMENT =========="
                    
                    # 1. Download active task definition version layout specifications from AWS
                    aws ecs describe-task-definition --task-definition ${ECS_TASK_FAMILY} --region ${AWS_REGION} --query taskDefinition > task-def.json
                    
                    # 2. Extract only the valid fields needed to register a new task definition layout configuration
                    jq '. | {family, containerDefinitions, volumes, networkMode, placementConstraints, requiresCompatibilities, cpu, memory, taskRoleArn, executionRoleArn}' task-def.json > cleaned-task-def.json
                    
                    # 3. Use standard Linux sed to find the old image URI and swap it for your fresh build number tag variation
                    # This replaces whatever image string is in there with your exact new build URI
                    sed -i 's|"image": ".*"|"image": "${ECR_URI}:${IMAGE_TAG}"|g' cleaned-task-def.json
                    
                    # 4. Push and register the new version modification to AWS registry systems
                    aws ecs register-task-definition --cli-input-json file://cleaned-task-def.json --region ${AWS_REGION} > registered-task.json

                    # 5. Update service configuration pointing explicitly to the brand new definition revision variant
                    aws ecs update-service \
                    --cluster ${ECS_CLUSTER} \
                    --service ${ECS_SERVICE} \
                    --task-definition ${ECS_TASK_FAMILY} \
                    --force-new-deployment \
                    --region ${AWS_REGION}
                    
                    # Clean up workspace temporary JSON files
                    rm -f task-def.json cleaned-task-def.json registered-task.json
                """
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
            echo ' ECS Deployment Triggered with Dynamic Revision Tracking'
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
