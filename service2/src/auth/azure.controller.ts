import { Controller, Get, Post, Request, UseGuards } from '@nestjs/common';
import { JwtService } from '@nestjs/jwt';
import { AuthGuard } from '@nestjs/passport';
import { OidcGuard } from './oidc.guard';

@Controller('auth/azure')
export class AzureController {
  constructor(private jwtService: JwtService) {}

  @Get()
  @UseGuards(OidcGuard)
  async googleAuth(@Request() req) {
    console.log(req.user)
    const payload = { username: req.user.userinfo.name, email: req.user.userinfo.email };
    return {
      access_token: this.jwtService.sign(payload)
    };
  }

  @Post()
  @UseGuards(OidcGuard)
  async azureauth(@Request() req) {
    console.log(req.user)
    const payload = { username: req.user.userinfo.name, email: req.user.userinfo.email };
    return {
      access_token: this.jwtService.sign(payload)
    };
  }  
}