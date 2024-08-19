import { IsString } from "class-validator"

export class CreateEventDto {
    @IsString()
    action: string
    @IsString()
    deviceId: string
    userId?: number
    targetUserId?: number
    message?: string
}
