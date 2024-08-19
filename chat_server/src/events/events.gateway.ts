import { WebSocketGateway, SubscribeMessage, MessageBody, WebSocketServer, ConnectedSocket } from '@nestjs/websockets';
import { EventsService } from './events.service';
import { CreateEventDto } from './dto/create-event.dto';
import { Server, Socket } from 'socket.io';
import { UsePipes, ValidationPipe } from '@nestjs/common';

class Client {
  client: Socket
  id: number
}

type MapClient = Record<string, Client>;

export const clients: MapClient = {}

@WebSocketGateway({
  transports: ['websocket'],
  cors: {
    origin: '*',
  },
})
export class EventsGateway {
  constructor(private readonly eventsService: EventsService) { }

  @WebSocketServer()
  server: Server;

  @SubscribeMessage('events')
  create(@MessageBody() body: any, @ConnectedSocket() client: Socket) {
    console.log(body);

    const dto = JSON.parse(body) as CreateEventDto
    console.log(dto, dto.action == 'register', dto.action);
    if (dto.action == 'register') {

      const id = (new Date()).getTime()
      console.log(id);

      clients[dto.deviceId] = {
        client,
        id
      }
    }
    else if (dto.action == 'sendNotification') {
      const lst = Object.values(clients)

      console.log(lst);

      lst.forEach((e) => console.log(e.id))

      const target = lst.find((e) => {
        console.log(e.id, dto.targetUserId);

        return e.id == dto.targetUserId
      })
      console.log(target);
      if (target) {
        target.client.write({ action: "com.hodoan.CUSTOM_ACTION", message: dto.message })
      }
    }
    return this.eventsService.create(dto);
  }
}
