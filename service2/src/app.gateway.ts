import { SubscribeMessage, WebSocketGateway, WebSocketServer, WsResponse } from "@nestjs/websockets";
import { Server, Socket } from "socket.io";

@WebSocketGateway()
export class AppGateway{
    @WebSocketServer() wss: Server;
    
    @SubscribeMessage('member')
    handleMessage(client:Socket, text:string): WsResponse<string>{
        return {event: 'member', data: text}
    }

}