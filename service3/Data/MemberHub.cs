namespace service3.Data;
using Microsoft.AspNetCore.SignalR;
public class MemberHub : Hub
{
    public async Task SendMessage(Member member)
    {
        await Clients.All.SendAsync("ReceiveMessage", member);
    }
}