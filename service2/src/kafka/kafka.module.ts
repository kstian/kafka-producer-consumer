import { Module } from '@nestjs/common';
import { AppGateway } from 'src/app.gateway';
import { KafkaService } from './kafka.service';

@Module({
    imports: [AppGateway],
    providers: [KafkaService, AppGateway]
})
export class KafkaModule {}
