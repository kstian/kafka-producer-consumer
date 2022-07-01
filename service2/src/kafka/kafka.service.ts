import { Injectable, OnModuleDestroy, OnModuleInit } from '@nestjs/common';
import { Consumer, Kafka } from 'kafkajs';
import { AppGateway } from 'src/app.gateway';

@Injectable()
export class KafkaService implements OnModuleInit, OnModuleDestroy {
  private kafka: Kafka;
  private consumer: Consumer;
  constructor(private readonly appGateway: AppGateway) {
    this.kafka = new Kafka({
      clientId: 'service2',
      brokers: ['192.168.56.100:9093'],
    });
    this.consumer = this.kafka.consumer({
      groupId: 'service2-consumer',
    });
  }
  onModuleDestroy() {
    this.consumer.disconnect();
  }

  async onModuleInit(): Promise<void> {
    await this.consumer.connect();
    await this.consumer.subscribe({ topic: 'member', fromBeginning: true });
    await this.consumer.run({
      eachMessage: async ({ topic, partition, message }) => {
        console.log(message.value.toString());
        this.appGateway.wss.emit('member', message.value.toString());
      },
    });
  }
}
