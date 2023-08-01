import * as lambda from 'aws-cdk-lib/aws-lambda';
import * as cdk from 'aws-cdk-lib/core';

export class LambdaStack extends cdk.Stack {
    constructor(scope: cdk.App, id: string, props?: cdk.StackProps) {
        super(scope, id, props);

        const func = new lambda.Function(this, 'LambdaFunction', {
            code: lambda.Code.fromAsset('lambda'),
            handler: 'com.example.MyHandler',
            runtime: lambda.Runtime.JAVA_17,
            tracing: lambda.Tracing.ACTIVE,
            environment: {
                OTEL_RESOURCE_ATTRIBUTES: 'service.name=MySampleApp',
                OTEL_EXPORTER: 'aws_xray',
            },
        });
    }
}

const app = new cdk.App();
new LambdaStack(app, 'LambdaStack', {
    env: {
        account: process.env.CDK_DEFAULT_ACCOUNT,
        region: process.env.CDK_DEFAULT_REGION,
    },
});
