package se.groupone.ecommerce.webservice;

import java.net.URI;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import se.groupone.ecommerce.model.Product;
import se.groupone.ecommerce.model.ProductParameters;
import se.groupone.ecommerce.service.ShopService;

@Path("products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductService
{
	@Context
	ServletContext context;
	@Context
	private UriInfo uriInfo;

	//  Skapa en ny produkt – detta ska returnera en länk till den skapade
	// produkten i Location-headern
	@POST
	public Response createProduct(ProductParameters productParameters)
	{
		ShopService shopService = (ShopService) context.getAttribute("ss");

		Product product = shopService.addProduct(productParameters);
		final URI location = uriInfo.getAbsolutePathBuilder().path(String.valueOf(product.getId())).build();

		return Response.created(location).build();
	}

	//  Hämta alla produkter
	@GET
	public Response getProducts()
	{
		ShopService shopService = (ShopService) context.getAttribute("ss");
		ArrayList<Product> products = (ArrayList<Product>) shopService.getProducts();
		
		return Response.ok(new GenericEntity<ArrayList<Product>>(products){}).build();
	}

	//  Hämta en produkt med ett visst id
	@GET
	@Path("{productId}")
	public Response getProduct(@PathParam("productId") final String productId)
	{
		ShopService shopService = (ShopService) context.getAttribute("ss");
		try
		{
			int productIdInt = Integer.parseInt(productId);
			Product product = shopService.getProductWithId(productIdInt);

			return Response.ok(product).build();
		}
		catch (NumberFormatException e)
		{
			return Response.status(Status.BAD_REQUEST).entity("Product id must be parsable to an integer.").build();
		}
	}

	//  Uppdatera en produkt
	@PUT
	@Path("{productId}")
	public Response putProduct(@PathParam("productId") final String productId, final ProductParameters productParameters)
	{
		ShopService shopService = (ShopService) context.getAttribute("ss");
		try
		{
			int productIdInt = Integer.parseInt(productId);
			shopService.updateProduct(productIdInt, productParameters);

			return Response.status(Status.NO_CONTENT).build();
		}
		catch (NumberFormatException e)
		{
			return Response.status(Status.BAD_REQUEST).entity("Product id must be parsable to an integer.").build();
		}
	}

	//  Ta bort en produkt (eller sätta den som inaktiv)
	@DELETE
	@Path("{productId}")
	public Response deleteProduct(@PathParam("productId") final String productId)
	{
		ShopService shopService = (ShopService) context.getAttribute("ss");
		try
		{
			int productIdInt = Integer.parseInt(productId);
			shopService.removeProduct(productIdInt);
			return Response.noContent().build();
		}
		catch (NumberFormatException e)
		{
			return Response.status(Status.BAD_REQUEST).entity("Product id must be parsable to an integer.").build();
		}
	}
}
