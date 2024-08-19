import { Controller, Get, Post, Body, Patch, Param, Delete } from '@nestjs/common';
import { PushService } from './push.service';
import { CreatePushDto } from './dto/create-push.dto';
import { UpdatePushDto } from './dto/update-push.dto';

const net = require('net');

@Controller('push')
export class PushController {
  constructor(private readonly pushService: PushService) { }

  @Post()
  create(@Body() createPushDto: CreatePushDto) {
    return this.pushService.create(createPushDto);
  }

  @Get()
  findAll() {
    const notificationMessage = JSON.stringify({
      action: 'sendNotification',
      targetDeviceId: 'your_device_id', // Replace with target device ID
      notification: 'Hello from servfff!'
    });

    const socket = net.createConnection({ port: 12345, host: '127.0.0.1' }, () => {
      socket.write(notificationMessage);
    });
    return this.pushService.findAll();
  }

  @Get(':id')
  findOne(@Param('id') id: string) {
    return this.pushService.findOne(+id);
  }

  @Patch(':id')
  update(@Param('id') id: string, @Body() updatePushDto: UpdatePushDto) {
    return this.pushService.update(+id, updatePushDto);
  }

  @Delete(':id')
  remove(@Param('id') id: string) {
    return this.pushService.remove(+id);
  }
}
