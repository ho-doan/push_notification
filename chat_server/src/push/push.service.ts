import { Injectable } from '@nestjs/common';
import { CreatePushDto } from './dto/create-push.dto';
import { UpdatePushDto } from './dto/update-push.dto';

@Injectable()
export class PushService {
  create(createPushDto: CreatePushDto) {
    return 'This action adds a new push';
  }

  findAll() {
    return `This action returns all push`;
  }

  findOne(id: number) {
    return `This action returns a #${id} push`;
  }

  update(id: number, updatePushDto: UpdatePushDto) {
    return `This action updates a #${id} push`;
  }

  remove(id: number) {
    return `This action removes a #${id} push`;
  }
}
