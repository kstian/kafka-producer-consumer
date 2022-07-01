import { Module } from '@nestjs/common';
import { AppController } from './app.controller';
import { AppGateway } from './app.gateway';
import { AppService } from './app.service';
import { AuthModule } from './auth/auth.module';
import { KafkaModule } from './kafka/kafka.module';

@Module({
  imports: [AuthModule, KafkaModule],
  controllers: [AppController],
  providers: [AppService, AppGateway],
})
export class AppModule {}
