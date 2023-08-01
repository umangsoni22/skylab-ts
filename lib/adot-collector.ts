import * as ec2 from 'aws-cdk-lib/aws-ec2';
import * as iam from 'aws-cdk-lib/aws-iam';
import * as cdk from 'aws-cdk-lib/core';

export class AdotCollectorStack extends cdk.Stack {
  constructor(scope: cdk.App, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    const vpc = new ec2.Vpc(this, 'Vpc', { maxAzs: 2 });

    const instance = new ec2.Instance(this, 'Instance', {
      vpc,
      instanceType: ec2.InstanceType.of(ec2.InstanceClass.T2, ec2.InstanceSize.MICRO),
      machineImage: new ec2.AmazonLinuxImage(),
      role: this.createInstanceRole(),
    });

    instance.userData.addCommands(
        "curl -O https://aws-otel-collector.s3.amazonaws.com/aws-otel-collector-latest.amd64.rpm",
        "sudo rpm -U ./aws-otel-collector-latest.amd64.rpm",
        "sudo systemctl start aws-otel-collector",
        "sudo systemctl enable aws-otel-collector"
    );
  }

  private createInstanceRole(): iam.Role {
    const role = new iam.Role(this, 'InstanceRole', {
      assumedBy: new iam.ServicePrincipal('ec2.amazonaws.com'),
    });

    role.addToPolicy(new iam.PolicyStatement({
      effect: iam.Effect.ALLOW,
      actions: [
        "xray:PutTraceSegments",
        "xray:PutTelemetryRecords",
        "xray:GetSamplingRules",
        "xray:GetSamplingTargets",
        "xray:GetSamplingStatisticSummaries"
      ],
      resources: ["*"],
    }));

    new iam.CfnInstanceProfile(this, 'InstanceProfile', { roles: [role.roleName] });

    return role;
  }
}

const app = new cdk.App();
new AdotCollectorStack(app, 'AdotCollectorStack', {
  env: {
    account: process.env.CDK_DEFAULT_ACCOUNT,
    region: process.env.CDK_DEFAULT_REGION,
  },
});
