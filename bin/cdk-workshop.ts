#!/usr/bin/env node
import * as cdk from 'aws-cdk-lib';
import { S3LambdaApiStack } from '../lib/cdk-workshop-stack';

const app = new cdk.App();
new S3LambdaApiStack(app, 'S3LambdaApiStack');
