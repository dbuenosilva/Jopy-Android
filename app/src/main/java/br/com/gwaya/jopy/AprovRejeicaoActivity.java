package br.com.gwaya.jopy;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

public class AprovRejeicaoActivity extends ActionBarActivity {
	
	public final static String STATUS_REJEITADO = "rejeitado";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_aprov_rejeicao);
		
		String jsonMyObject = "";
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		   jsonMyObject = extras.getString(DetalheActivity.PEDIDO);
		}
		
		final PedidoCompra pedido = new Gson().fromJson(jsonMyObject, PedidoCompra.class);
		
		setTitle(pedido.nomeForn);
		
		final EditText editText = (EditText) findViewById(R.id.editTextMotivRej);
		
		final Button buttonCancelaConf = (Button) findViewById(R.id.buttonCancelaConf);
		final Button buttonConfirmar = (Button) findViewById(R.id.buttonConfirmar);
		
		//final FragmentManager fmgr = getFragmentManager();
		
		final Context context = getApplicationContext();
		final int duration = Toast.LENGTH_SHORT;

		buttonCancelaConf.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					Toast toast = Toast.makeText(context, "Rejeição Cancelada!", duration);
					toast.show();
					AprovRejeicaoActivity.this.finish();
				} catch (Exception e) {
					
				}
			}
		});
		
		buttonConfirmar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					FilaPedidoCompraDataSource filaDataSource =
							new FilaPedidoCompraDataSource(AprovRejeicaoActivity.this);

					pedido.statusPedido = STATUS_REJEITADO;
					pedido.motivoRejeicao = editText.getText().toString();

					filaDataSource.open();
					filaDataSource.createFilaPedidoCompra(pedido);
					filaDataSource.close();

					Toast toast = Toast.makeText(context, "Rejeição Confirmada!", duration);
					toast.show();

					AprovRejeicaoActivity.this.finish();

				} catch (Exception e) {

				}
			}
		});
		
	}
}
