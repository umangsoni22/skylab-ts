import * as cdk from 'aws-cdk-lib';
import * as lambda from 'aws-cdk-lib/aws-lambda';
import * as apigateway from 'aws-cdk-lib/aws-apigateway';
import * as iam from 'aws-cdk-lib/aws-iam';
import * as s3 from 'aws-cdk-lib/aws-s3';
import * as s3deploy from 'aws-cdk-lib/aws-s3-deployment';
import * as signer from 'aws-cdk-lib/aws-signer';


import { readFileSync } from 'fs';

export class S3LambdaApiStack extends cdk.Stack {
  constructor(app: cdk.App, id: string, props?: cdk.StackProps) {
    super(app, id, props);

    // Define the Lambda function
    const s3ListFunction = new lambda.Function(this, 'S3ListFunction', {
      runtime: lambda.Runtime.PYTHON_3_8,
      handler: 'handler.handler',
      code: lambda.Code.fromAsset('lambda',{
        bundling: {
          command: [
            'bash', '-c',
            'pip install aws_xray_sdk -t /asset-output && cp -au . /asset-output'
          ],
          image: lambda.Runtime.PYTHON_3_8.bundlingImage,
        }
      }),
      tracing: lambda.Tracing.ACTIVE, // Enable X-Ray tracing
      timeout: cdk.Duration.seconds(30),
    });

    // Define an IAM policy statement which allows the Lambda function to list objects in S3 buckets
    s3ListFunction.addToRolePolicy(new iam.PolicyStatement({
      actions: ['s3:ListBucket'],
      resources: ['arn:aws:s3:::*'],
    }));


    // Define an IAM policy statement which allows the Lambda function to send traces to X-Ray
    s3ListFunction.addToRolePolicy(new iam.PolicyStatement({
      actions: ['xray:PutTraceSegments', 'xray:PutTelemetryRecords'],
      resources: ['*'],
    }));

    // Define an IAM policy statement which allows the Lambda function to sign API Gateway requests
    s3ListFunction.addToRolePolicy(new iam.PolicyStatement({
      actions: ['apigateway:*'],
      resources: ['*'],
    }));
    
    // Define the API Gateway
    const api = new apigateway.RestApi(this, 'S3BucketListApi', {
      restApiName: 'S3 Bucket List Service',
    });

    // Define a resource for the API Gateway
    const bucketList = api.root.addResource('list_objects').addResource('{bucket}');

    // Define a GET method for the API Gateway resource
    const getIntegration = new apigateway.LambdaIntegration(s3ListFunction);
    bucketList.addMethod('GET', getIntegration);

    // Create a deployment
    const deployment = new apigateway.Deployment(this, 'Deployment', {
      api: api,
    });

    // Create a stage with X-Ray enabled
    new apigateway.CfnStage(this, 'Stage', {
      stageName: `${this.stackName}-prod`,
      restApiId: api.restApiId,
      deploymentId: deployment.deploymentId,
      tracingEnabled: true,
    });

    // Create a S3 bucket
    const bucket = new s3.Bucket(this, 'SampleBucket', {
      removalPolicy: cdk.RemovalPolicy.DESTROY,
    });

    // Create a couple of text files and upload them to the bucket
    new s3deploy.BucketDeployment(this, 'DeployFiles', {
      sources: [s3deploy.Source.asset('./assets')],
      destinationBucket: bucket,
    });
  }
}
