# lambda/handler.py

import json
from time import sleep
import boto3
from botocore.exceptions import ClientError
from aws_xray_sdk.core import xray_recorder

def handler(event, context):
    s3 = boto3.client('s3')

    try:     
        with xray_recorder.in_subsegment('## Manual - S3 List Objects'):
            
            bucket = event['pathParameters']['bucket']

            xray_recorder.put_annotation('Bucket', str(bucket))
            response = s3.list_objects_v2(Bucket=bucket)

            sleep(3) # Simulate additional operation
            return {
                'statusCode': 200,
                'body': json.dumps([item['Key'] for item in response.get('Contents', [])])
            }
    except ClientError as e:
        return {
            'statusCode': 500,
            'body': str(e)
        }
