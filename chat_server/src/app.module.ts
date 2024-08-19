import { Module } from '@nestjs/common';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { EventsModule } from './events/events.module';
import { PushModule } from './push/push.module';

@Module({
  imports: [EventsModule, PushModule],
  controllers: [AppController],
  providers: [AppService],
})
export class AppModule {}
