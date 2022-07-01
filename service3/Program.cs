using Microsoft.AspNetCore.ResponseCompression;
using Microsoft.AspNetCore.Components;
using Microsoft.AspNetCore.Components.Web;
using service3.Data;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.
builder.Services.AddRazorPages();
builder.Services.AddServerSideBlazor();
builder.Services.AddSignalR();
builder.Services.AddSingleton<MemberHub>();
builder.Services.AddHostedService<KafkaConsumerHandler>();
builder.Services.AddResponseCompression(opts =>
{
    opts.MimeTypes = ResponseCompressionDefaults.MimeTypes.Concat(
        new[] { "application/octet-stream" });
});
builder.Services.AddAntDesign();

var app = builder.Build();
app.UseResponseCompression();
// Configure the HTTP request pipeline.
if (!app.Environment.IsDevelopment())
{
    app.UseExceptionHandler("/Error");
}


app.UseStaticFiles();

app.UseRouting();

app.MapBlazorHub();
app.MapHub<MemberHub>("/member");
app.MapFallbackToPage("/_Host");

app.Run();
