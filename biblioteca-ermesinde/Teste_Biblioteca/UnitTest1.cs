
using Biblioteca_Ermesinde.Controllers;
using Biblioteca_Ermesinde.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using Moq;
namespace Teste_Biblioteca
{
    public class UnitTest1
    {
        [Fact]
        public void GetCliente_Existente_DeveRetornarOk()
        {
            // Arrange
            var loggerMock = new Mock<ILogger<ClientesController>>();
            var clientesController = new ClientesController(loggerMock.Object);

            // Act
            var resultado = clientesController.GetCliente(1) as OkObjectResult;

            // Assert
            Assert.NotNull(resultado);
            Assert.Equal(200, resultado.StatusCode);

            var clienteRetornado = resultado.Value as Cliente;
            Assert.NotNull(clienteRetornado);
            Assert.Equal(1, clienteRetornado.Id);
        }

        [Fact]
        public void GetCliente_NaoExistente_DeveRetornarNotFound()
        {
            // Arrange
            var loggerMock = new Mock<ILogger<ClientesController>>();
            var clientesController = new ClientesController(loggerMock.Object);

            // Act
            var resultado = clientesController.GetCliente(99) as NotFoundObjectResult;

            // Assert
            Assert.NotNull(resultado);
            Assert.Equal(404, resultado.StatusCode);
            Assert.Equal("Cliente com ID 99 não foi encontrado.", resultado.Value);
        }

        [Fact]
        public void GetLivro_DeveRetornarLivroCorreto()
        {
            // Arrange
            var loggerMock = new Mock<ILogger<LivroController>>();

            var livrosController = new LivroController(loggerMock.Object);

            // Act
            var resultado = livrosController.GetLivro(1) as OkObjectResult;

            // Assert
            Assert.NotNull(resultado);
            Assert.Equal(200, resultado.StatusCode);

            var livrosDevolvido = resultado.Value as Livro;
            Assert.NotNull(livrosDevolvido);
            Assert.Equal(1, livrosDevolvido.Id);
        }

        [Fact]
        public void GetLivro_NaoExistente_DeveRetornarNotFound()
        {
            // Arrange
            var loggerMock = new Mock<ILogger<LivroController>>();
            var livrosController = new LivroController(loggerMock.Object);

            // Act
            var resultado = livrosController.GetLivro(99) as NotFoundObjectResult;

            // Assert
            Assert.NotNull(resultado);
            Assert.Equal(404, resultado.StatusCode);
            Assert.Equal("Livro com ID 99 não foi encontrado.", resultado.Value);
        }

        [Fact]
        public void GetReservaPorId_ExistingReserva_ReturnsOkResult()
        {
            // Arrange
            var logger = new Mock<ILogger<ReservaController>>();
            var clienteController = new ClientesController(null); // Pode ser necessário ajustar a injeção de dependência aqui
            var controller = new ReservaController(logger.Object, clienteController);
            var id = 1; // Deve existir uma reserva com este ID para o teste

            // Act
            var result = controller.GetReservaPorId(id) as OkObjectResult;

            // Assert
            Assert.NotNull(result);

            // Faça o cast direto para o tipo esperado
            var actualData = (ReservaResponse)result.Value;

            // Verifique as partes relevantes da resposta
            Assert.Equal("Jao", actualData.NomeCliente, StringComparer.OrdinalIgnoreCase);
            Assert.Equal("O jao vai a escola", actualData.TituloLivro);
            Assert.Equal(new DateTime(2000, 5, 20), actualData.DataReserva);
        }



        [Fact]
        public void GetReservaPorId_NonExistingReserva_ReturnsNotFound()
        {
            // Arrange
            var logger = new Mock<ILogger<ReservaController>>();
            var clienteController = new ClientesController(null); // Pode ser necessário ajustar a injeção de dependência aqui
            var controller = new ReservaController(logger.Object, clienteController);
            var id = 99; // ID que não existe

            // Act
            var result = controller.GetReservaPorId(id) as NotFoundObjectResult;

            // Assert
            Assert.NotNull(result);

            // Verifique se a resposta real é um objeto anônimo com a propriedade 'Mensagem'
            Assert.IsType<NotFoundObjectResult>(result);

            // Obtenha a mensagem real do resultado
            var actualMessage = result.Value as string;

            // Verifique se a mensagem é nula
            Assert.Null(actualMessage);
        }

        [Fact]
        public void GetReservasPorCliente_ReturnsCorrectReservas()
        {
            // Arrange
            var logger = new Mock<ILogger<ReservaController>>();

            // Crie uma instância real do ClientesController com um logger diferente
            var clienteLogger = new Mock<ILogger<ClientesController>>();
            var clienteController = new ClientesController(clienteLogger.Object);

            var controller = new ReservaController(logger.Object, clienteController);
            var idCliente = 1; // Suponha que existe um cliente com este ID para o teste

            // Act
            var result = controller.GetReservasPorCliente(idCliente) as OkObjectResult;

            // Assert
            Assert.NotNull(result);
        }
    }

   
}