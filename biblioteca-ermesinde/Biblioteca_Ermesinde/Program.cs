using Biblioteca_Ermesinde.Models;
using Microsoft.EntityFrameworkCore;
using Biblioteca_Ermesinde.Controllers;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.
builder.Services.AddControllers();

// Add DbContext to the container
builder.Services.AddDbContext<BackEndContext>(options =>
{
    // Configure as opções do DbContext, como a cadeia de conexão
    options.UseSqlServer(builder.Configuration.GetConnectionString("DefaultConnection"));
});

// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

// Add your services here
builder.Services.AddScoped<ClientesController>(); // Adicione outros servi�os conforme necess�rio

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseHttpsRedirection();
app.UseAuthorization();
app.MapControllers();

app.Run();
