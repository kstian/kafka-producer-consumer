namespace service3.Data;

using Confluent.Kafka;
using Microsoft.Extensions.Hosting;
using System;
using System.Threading;
using System.Threading.Tasks;
using System.Text.Json;
using Microsoft.AspNetCore.SignalR.Client;

public class KafkaConsumerHandler : BackgroundService {
    private readonly string topic = "member";
    private readonly MemberHub _memberHub;
    private readonly ILogger<KafkaConsumerHandler> _logger;
    private JsonSerializerOptions options = new JsonSerializerOptions{
        PropertyNameCaseInsensitive = true,
    };
    public KafkaConsumerHandler(MemberHub memberHub, ILogger<KafkaConsumerHandler> logger):base(){
        _memberHub = memberHub;
        _logger = logger;
    }
    protected override async Task ExecuteAsync(CancellationToken stopToken)
    {
      //Do your preparation (e.g. Start code) here
      System.Console.WriteLine("Start listneing");
      var conf = new ConsumerConfig
        {
            GroupId = "service3-consumer",
            BootstrapServers = "192.168.56.100:9092"
        };
        await Task.Yield();
        using (var builder = new ConsumerBuilder<Ignore,String>(conf).Build())
        {
            builder.Subscribe(topic);
            var cancelToken = new CancellationTokenSource();
            try
            {
                while (!stopToken.IsCancellationRequested) {
                    var consumer = builder.Consume(stopToken);
                    _logger.LogInformation($"Message: {consumer.Message.Value} received from {consumer.TopicPartitionOffset}");
                    Member? member = JsonSerializer.Deserialize<Member>(consumer.Message.Value, options);
                    await _memberHub.SendMessage(member);       
                }
            }
            catch (Exception)
            {
                builder.Close();
            }
        }
        
    }
}

public record Member{
    public string Id { get; set; } = default!;
    public string FirstName { get; set; } = default!;
    public string LastName { get; set; } = default!;
};