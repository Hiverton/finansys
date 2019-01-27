public String consultarNotasOrigem(String jsonStr, String tpoConsulta) throws Exception {
    CallableStatement stmt = null;
    Connection conn = null;
    ResultSet rs = null;
    StringBuffer st = new StringBuffer();
    Connection flatConn = null;
    Struct structType;

    try {

        conn = getConnectionNovo(4);
        Method m = conn.getClass().getMethod("getUnderlyingConnection");
        m.setAccessible(true);
        flatConn = (Connection) m.invoke(conn, (Object[]) null);

        Map<String, List<Struct>> mNotas = this.obterOBjectStructNotasDevolucao(flatConn, jsonStr, tpoConsulta);
        structType = this.obterOBjectStructDevolucao2(flatConn, jsonStr, mNotas.get("notas"), "NOTAS");
        String sql = "{ call " + PropertiesReader.getProperty(PropertiesReader.PACKAGE_DEVOLUCAO_MULTI_EMPRESA2)
                + ".prc_retornaListaNotasPS(?,?,?,?,?)}";
        stmt = conn.prepareCall(sql);

        stmt.setObject(1, structType, OracleTypes.STRUCT);
        stmt.registerOutParameter(2, OracleTypes.CURSOR);
        stmt.registerOutParameter(3, OracleTypes.CURSOR);
        stmt.registerOutParameter(4, OracleTypes.NUMBER);
        stmt.registerOutParameter(5, OracleTypes.VARCHAR);
        stmt.execute();

        if (stmt.getInt(4) > 0) {
            throw new Exception(tratarRetornoErro(stmt.getInt(4), stmt.getString(5)));
        }
        st.append("'cod_erro':'0','listNotas':[");
        rs = (ResultSet) stmt.getObject(2);
        int i = 0;
        while (rs.next()) {
            i++;
            st.append("{'tpo_nota':'").append(rs.getString("TpoNota")).append("',");
            st.append("'empresa':'").append(rs.getString("Nom_Empresa_Grupo")).append("',");
            st.append("'id_empresa':'").append(rs.getString("Id_Empresa_Grupo")).append("',");
            st.append("'cod_local':'").append(rs.getString("cod_local")).append("',");
            st.append("'ano_nota':'").append(rs.getString("Num_Ano_Nf")).append("',");
            st.append("'nro_nota':'").append(rs.getString("NumNota")).append("',");
            st.append("'valor_nota':'").append(rs.getString("VlrTotalNota")).append("',");
            st.append("'dta_nota':'").append(rs.getString("DtaEmissao")).append("',");
            // st.append("'id':'").append("1_20_01_16_1123_17/12/2212_ATICA_XXXXXXXXXX").append("',");
            st.append("'id':'").append(montarId(rs)).append("',");
            st.append("'pedido_educ':'").append(rs.getString("pedido_educ")).append("',");
            st.append("'chave':'").append(rs.getString("CodChave")).append("'}, ");
        }
        if (i > 0) {
            st.replace(st.length() - 2, st.length(), "]");
        }
        if (i == 0) {
            st.append("]");
        }

    } catch (Exception e) {
        throw new Exception(tratarRetornoErro(100, e.getMessage()));
    } finally {
        closeConnectionNova(conn);
        closeCallableStatementNovo(stmt);
        closeResultSetNovo(rs);
    }

    return st.toString();
}

private Struct obterOBjectStructDevolucao2(Connection flatConn, String jsonStr, List<Struct> lstNotas,String tpoConsulta) throws Exception {
    String nomeType = "";

    // TODO MUDAR PARA 45
    int tamanhoArray = 45;
    try {
        nomeType = "xxDevolucao";

        StructDescriptor structdescCabec = null;
        synchronized (this) {
            structdescCabec = StructDescriptor.createDescriptor(nomeType.toUpperCase(), flatConn);
        }

        Object oArray[] = new Object[tamanhoArray];
        JSONParser parser = new JSONParser();
        JSONObject cabec = new JSONObject();
        if (tpoConsulta.equals("ITENS")) {
            int i = 0;
            while (i < tamanhoArray) {
                oArray[i] = null;
                i++;
            }
            jsonStr = jsonStr.replaceAll("\\\"", "");
            jsonStr = jsonStr.replaceAll("'", "\\\"");
            cabec = (JSONObject) parser.parse(jsonStr);

            JSONArray notasJson = ((JSONArray) cabec.get("LISTA_NOTAS"));
            JSONObject item = null;
            item = (JSONObject) notasJson.get(0);
            oArray[0] = Integer.parseInt(item.get("ID_FILIAL").toString()); // id_filial number
            oArray[1] = item.get("NRO_DEVOLUCAO").equals("") ? null
                    : new Integer(item.get("NRO_DEVOLUCAO").toString());
            oArray[3] = Integer.parseInt(item.get("TPO_DEVOLUCAO").toString()); // ,cod_tipo_devolucao number
            oArray[36] = item.get("COD_LOCAL").toString().equals("") ? null
                    : Integer.parseInt(item.get("COD_LOCAL").toString());

        } else {
            jsonStr = jsonStr.replaceAll("\\\"", "");
            jsonStr = jsonStr.replaceAll("'", "\\\"");
            cabec = (JSONObject) parser.parse(jsonStr);

            oArray[0] = cabec.get("ID_FILIAL") == null ? null : Integer.parseInt(cabec.get("ID_FILIAL").toString()); // id_filial
                                                                                                                        // number
            oArray[1] = cabec.get("NRO_DEVOLUCAO") == null ? null
                    : cabec.get("NRO_DEVOLUCAO").equals("") ? null
                            : new Integer(cabec.get("NRO_DEVOLUCAO").toString());
            oArray[2] = null;
            oArray[3] = cabec.get("TPO_DEVOLUCAO") == null ? null
                    : Integer.parseInt(cabec.get("TPO_DEVOLUCAO").toString()); // ,cod_tipo_devolucao number
            oArray[4] = null;
            oArray[5] = null;
            oArray[6] = null;
            oArray[7] = cabec.get("CNPJ_CLIENTE") == null ? null
                    : Integer.parseInt(cabec.get("CNPJ_CLIENTE").toString()); // ,cod_tipo_devolucao number
            oArray[8] = cabec.get("FILIAL_CLIENTE") == null ? null
                    : Integer.parseInt(cabec.get("FILIAL_CLIENTE").toString()); // ,cod_tipo_devolucao number

            int i = 9;
            while (i < tamanhoArray) {
                oArray[i] = null;
                i++;
            }
            oArray[36] = cabec.get("COD_LOCAL") == null ? null
                    : cabec.get("COD_LOCAL").toString().equals("") ? null
                            : Integer.parseInt(cabec.get("COD_LOCAL").toString()); // ,COD_LOCAL number

        }
        if (tpoConsulta.equals("NOTAS")) {
            oArray[36] = cabec.get("COD_LOCAL") == null ? null
                    : cabec.get("COD_LOCAL").toString().equals("") ? null
                            : Integer.parseInt(cabec.get("COD_LOCAL").toString());
            oArray[37] = null;
            oArray[38] = null;
            oArray[39] = null;
            oArray[40] = null;

        } else if (tpoConsulta.equals("ITENS")) {
            oArray[41] = lstNotas.toArray(); // ,notas_devolucao xxDevolucao_notas_table,
        }
        oArray[42] = null;
        oArray[43] = null;
        oArray[44] = null;

        STRUCT struct = new STRUCT(structdescCabec, flatConn, oArray);

        return struct;

    } catch (Exception e) {
        throw new Exception("Erro ao montar struct de Devolução. MSg: " + e.getMessage());
    }
}

private Map<String, List<Struct>> obterOBjectStructItemDevolucao(Connection flatConn, String jsonStr) throws Exception {
		List<Struct> lstItem = null;
		Map<String, List<Struct>> mItens = new HashMap<String, List<Struct>>();
		SimpleDateFormat sdf;
		Object aux;

		StructDescriptor structdescItem = null;
		synchronized (this) {
			structdescItem = StructDescriptor.createDescriptor(new String("xxDevolucao_item").toUpperCase(), flatConn);
		}

		Object oArray[] = new Object[55];
		String nameType = "";
		JSONParser parser = new JSONParser();
		jsonStr = jsonStr.replaceAll("\\\"", "");
		jsonStr = jsonStr.replaceAll("'", "\\\"");
		JSONObject cabec = (JSONObject) parser.parse(jsonStr);
		JSONArray itensJson = ((JSONArray) cabec.get("LISTA_ITENS_SIMULADO"));
		nameType = "LISTA_ITENS_SIMULADO";
		if (itensJson.isEmpty()) {
			itensJson = ((JSONArray) cabec.get("LISTA_ITENS_CONS"));
			nameType = "LISTA_ITENS_CONS";
			if (itensJson.isEmpty()) {
				itensJson = ((JSONArray) cabec.get("LISTA_ITENS_FEIRA"));
				nameType = "LISTA_ITENS_FEIRA";
			}
		}

		// valores que são iguais para todos os itens
		Object id_filial[] = this.toArrayOuNulo(cabec.get("ID_FILIAL"));
		Object num_devolucao[] = this.toArrayOuNulo(cabec.get("NRO_DEVOLUCAO"));
		Object cod_tipo_devolucao[] = this.toArrayOuNulo(cabec.get("TPO_DEVOLUCAO"));

		// valores que são dinamicos para os itens
		Object cod_produto[] = null;
		Object qtd_truncada_empresa[] = null;
		Object qtd_trucado_cliente[] = null;
		Object qtd_devolvida[] = null;
		Object qtd_recusada[] = null;
		Object num_credito[] = null;
		Object num_troca[] = null;
		Object desc_produto[] = null;
		Object num_isbn[] = null;
		Object nom_empresa[] = null;
		Object id_empresa_grupo[] = null;
		Object tpo_operacao[] = null;
		Object cod_local_nota[] = null;
		Object num_sequencial_nota[] = null;
		Object tpo_nota_fiscal[] = null;
		Object num_ano_nota_fiscal[] = null;
		Object qtd_item_nf[] = null;
		Object qtd_devolvida_nf[] = null;
		Object vlr_unitario[] = null;
		Object per_desconto[] = null;
		Object seq_item_consig[] = null;
		Object vlr_total_item[] = null;
		Object vlr_desconto[] = null;
		Object cod_sit_tributaria[] = null;
		Object tpo_trib_icms[] = null;
		Object vlr_base_calculo_icms[] = null;
		Object per_icms[] = null;
		Object vlr_icms_subst[] = null;
		Object vlr_icms_outros[] = null;
		Object vlr_icms_isento[] = null;
		Object cod_sit_trib_ipi[] = null;
		Object tpo_trib_ipi[] = null;
		Object vlr_base_calculo_ipi[] = null;
		Object per_ipi[] = null;
		Object vlr_ipi[] = null;
		Object vlr_ipi_outros[] = null;
		Object vlr_ipi_isento[] = null;
		Object tpo_produto_sia[] = null;
		Object cod_produto_sia[] = null;
		Object tpo_trib_pis_cofins[] = null;
		Object cod_sit_trib_pis_cofins[] = null;
		Object per_pis[] = null;
		Object per_cofins[] = null;
		Object vlr_base_calculo_pis[] = null;
		Object vlr_base_calculo_cofins[] = null;
		Object vlr_pis[] = null;
		Object vlr_cofins[] = null;
		Object vlr_icms[] = null;
		Object qtd_cliente[] = null;
		Object saldo_nota[] = null;
		Object qtd_faturado[] = null;

		// popular todos os itens na davolução
		JSONObject item = null;
		for (int i = 0; i < itensJson.size(); i++) {
			item = (JSONObject) itensJson.get(i);
			cod_produto = this.toArrayOuNulo(item.get("COD_CORP"));
			if (nameType.equals("LISTA_ITENS_VDD")) {
				qtd_truncada_empresa = this.toArrayOuNulo(null);
				qtd_trucado_cliente = this.toArrayOuNulo(null);
				qtd_devolvida = this.toArrayOuNulo(item.get("QTD_DEV"));
				qtd_recusada = this.toArrayOuNulo(null);
				num_credito = this.toArrayOuNulo(null);
				num_troca = this.toArrayOuNulo(null);
			} else if (nameType.equals("LISTA_ITENS_SIMULADO")) {
				try {
					String[] dadosConsig = buscarDadosNotaConsig(item.get("NF_ORIGEM").toString(), cod_produto,
							cabec.get("CNPJ_CLIENTE").toString(), cabec.get("FILIAL_CLIENTE").toString());
					seq_item_consig = this.toArrayOuNulo(dadosConsig[4].toString().equals("") ? null : dadosConsig[4]);
				} catch (Exception e) {
					seq_item_consig = null;
				}

				num_isbn = this.toArrayOuNulo(item.get("ISBN").toString().equals("") ? null : item.get("ISBN"));
				desc_produto = this
						.toArrayOuNulo(item.get("DESCRICAO").toString().equals("") ? null : item.get("DESCRICAO"));
				num_sequencial_nota = this
						.toArrayOuNulo(item.get("NF_ORIGEM").toString().equals("") ? null : item.get("NF_ORIGEM"));
				cod_local_nota = this
						.toArrayOuNulo(item.get("LOCAL_NOTA").toString().equals("") ? null : item.get("LOCAL_NOTA"));
				tpo_nota_fiscal = this
						.toArrayOuNulo(item.get("TPO_NOTA").toString().equals("") ? null : item.get("TPO_NOTA"));
				num_ano_nota_fiscal = this
						.toArrayOuNulo(item.get("ANO_NOTA").toString().equals("") ? null : item.get("ANO_NOTA"));
				qtd_item_nf = this.toArrayOuNulo(
						item.get("QTD_ITEM_NF_ORIGEM").toString().equals("") ? null : item.get("QTD_ITEM_NF_ORIGEM"));
				saldo_nota = this.toArrayOuNulo(item.get("SALDO").toString().equals("") ? null : item.get("SALDO"));
				qtd_devolvida = this.toArrayOuNulo(
						item.get("QTD_DEVOLVIDA").toString().equals("") ? null : item.get("QTD_DEVOLVIDA"));
				qtd_faturado = this.toArrayOuNulo(
						item.get("QTD_FATURADA").toString().equals("") ? null : item.get("QTD_FATURADA"));
				vlr_unitario = this
						.toArrayOuNulo(item.get("VLR_UNIT").toString().equals("") ? null : item.get("VLR_UNIT"));
				per_desconto = this.toArrayOuNulo(
						item.get("PER_DESCONTO").toString().equals("") ? null : item.get("PER_DESCONTO"));
				tpo_operacao = this.toArrayOuNulo(
						item.get("FLG_OPERACAO").toString().equals("") ? null : item.get("FLG_OPERACAO"));
				qtd_truncada_empresa = this
						.toArrayOuNulo(item.get("QTD_EMPRESA").toString().equals("") ? null : item.get("QTD_EMPRESA"));
				qtd_trucado_cliente = this
						.toArrayOuNulo(item.get("QTD_TRUNC").toString().equals("") ? null : item.get("QTD_TRUNC"));
				qtd_recusada = this
						.toArrayOuNulo(item.get("QTD_RECUSA").toString().equals("") ? null : item.get("QTD_RECUSA"));

				tpo_produto_sia = this.toArrayOuNulo(
						item.get("TPO_PRODUTO_SIA").toString().equals("") ? null : item.get("TPO_PRODUTO_SIA"));
				cod_produto_sia = this.toArrayOuNulo(
						item.get("COD_PRODUTO_SIA").toString().equals("") ? null : item.get("COD_PRODUTO_SIA"));

			} else if (nameType.equals("LISTA_ITENS_CONS")) {
				qtd_truncada_empresa = this
						.toArrayOuNulo(item.get("QTD_EMPRESA").toString().equals("") ? null : item.get("QTD_EMPRESA"));
				qtd_trucado_cliente = this
						.toArrayOuNulo(item.get("QTD_TRUNC").toString().equals("") ? null : item.get("QTD_TRUNC"));
				qtd_devolvida = this.toArrayOuNulo(
						item.get("QTD_DEVOLVIDA").toString().equals("") ? null : item.get("QTD_DEVOLVIDA"));
				qtd_recusada = this
						.toArrayOuNulo(item.get("QTD_RECUSA").toString().equals("") ? null : item.get("QTD_RECUSA"));
				num_credito = this.toArrayOuNulo(null);
				num_troca = this.toArrayOuNulo(null);
				desc_produto = this.toArrayOuNulo(
						item.get("DESC_PRODUTO").toString().equals("") ? null : item.get("DESC_PRODUTO"));
				num_isbn = this.toArrayOuNulo(item.get("ISBN").toString().equals("") ? null : item.get("ISBN"));
				nom_empresa = this.toArrayOuNulo(null);
				id_empresa_grupo = this.toArrayOuNulo(null);
				tpo_operacao = this.toArrayOuNulo(
						item.get("FLG_OPERACAO").toString().equals("") ? null : item.get("FLG_OPERACAO"));

				qtd_cliente = this
						.toArrayOuNulo(item.get("QTD_CLIENTE").toString().equals("") ? null : item.get("QTD_CLIENTE"));
				saldo_nota = this.toArrayOuNulo(item.get("SALDO").toString().equals("") ? null : item.get("SALDO"));

				if (!item.get("NF_CONSIG").toString().equals("")) {
					String[] dadosConsig = buscarDadosNotaConsig(item.get("NF_CONSIG").toString(), cod_produto,
							cabec.get("CNPJ_CLIENTE").toString(), cabec.get("FILIAL_CLIENTE").toString());
					cod_local_nota = this.toArrayOuNulo(dadosConsig[0].toString().equals("") ? null : dadosConsig[0]);
					num_sequencial_nota = this
							.toArrayOuNulo(dadosConsig[1].toString().equals("") ? null : dadosConsig[1]);
					tpo_nota_fiscal = this.toArrayOuNulo(dadosConsig[2].toString().equals("") ? null : dadosConsig[2]);
					num_ano_nota_fiscal = this
							.toArrayOuNulo(dadosConsig[3].toString().equals("") ? null : dadosConsig[3]);
					qtd_item_nf = this.toArrayOuNulo(dadosConsig[5].toString().equals("") ? null : dadosConsig[5]);
					seq_item_consig = this.toArrayOuNulo(dadosConsig[4].toString().equals("") ? null : dadosConsig[4]);
				} else {
					cod_local_nota = this.toArrayOuNulo(null);
					num_sequencial_nota = this.toArrayOuNulo(null);
					tpo_nota_fiscal = this.toArrayOuNulo(null);
					num_ano_nota_fiscal = this.toArrayOuNulo(null);
					qtd_item_nf = this.toArrayOuNulo(null);
					seq_item_consig = this.toArrayOuNulo(null);
				}
				if (cabec.get("LISTA_INF_FISCAIS") != null) {
					JSONArray itensDadosFiscaisJson = ((JSONArray) cabec.get("LISTA_INF_FISCAIS"));
					// popular todos os itens com dados Fiscais na davolução
					JSONObject dadosFiscais = null;
					for (int x = 0; x < itensDadosFiscaisJson.size(); x++) {
						dadosFiscais = (JSONObject) itensDadosFiscaisJson.get(x);
						if (cod_produto[0].toString().equals(dadosFiscais.get("COD_CORP").toString())) {
							qtd_devolvida_nf = this
									.toArrayOuNulo(dadosFiscais.get("FLG_OPERACAO").toString().equals("") ? null
											: dadosFiscais.get("FLG_OPERACAO"));
							vlr_unitario = this.toArrayOuNulo(dadosFiscais.get("VLR_UNIT").toString().equals("") ? null
									: dadosFiscais.get("VLR_UNIT"));
							per_desconto = this.toArrayOuNulo(dadosFiscais.get("DESCONTO").toString().equals("") ? null
									: dadosFiscais.get("DESCONTO"));
							vlr_total_item = this
									.toArrayOuNulo(dadosFiscais.get("VLR_TOTAL").toString().equals("") ? null
											: dadosFiscais.get("VLR_TOTAL"));
							vlr_desconto = this
									.toArrayOuNulo(dadosFiscais.get("FLG_OPERACAO").toString().equals("") ? null
											: dadosFiscais.get("FLG_OPERACAO"));
							cod_sit_tributaria = this
									.toArrayOuNulo(dadosFiscais.get("SIT_ICMS").toString().equals("") ? null
											: dadosFiscais.get("SIT_ICMS"));
							tpo_trib_icms = this.toArrayOuNulo(dadosFiscais.get("TPO_ICMS").toString().equals("") ? null
									: dadosFiscais.get("TPO_ICMS"));
							vlr_base_calculo_icms = this
									.toArrayOuNulo(dadosFiscais.get("BC_ICMS").toString().equals("") ? null
											: dadosFiscais.get("BC_ICMS"));
							per_icms = this.toArrayOuNulo(dadosFiscais.get("PER_ICMS").toString().equals("") ? null
									: dadosFiscais.get("PER_ICMS"));
							vlr_icms_subst = this
									.toArrayOuNulo(dadosFiscais.get("VLR_ICMS").toString().equals("") ? null
											: dadosFiscais.get("VLR_ICMS"));
							vlr_icms_outros = this
									.toArrayOuNulo(dadosFiscais.get("OUTROS_ICMS").toString().equals("") ? null
											: dadosFiscais.get("OUTROS_ICMS"));
							vlr_icms_isento = this
									.toArrayOuNulo(dadosFiscais.get("INSENTO_ICMS").toString().equals("") ? null
											: dadosFiscais.get("INSENTO_ICMS"));
							cod_sit_trib_ipi = this
									.toArrayOuNulo(dadosFiscais.get("SIT_IPI").toString().equals("") ? null
											: dadosFiscais.get("SIT_IPI"));
							tpo_trib_ipi = this.toArrayOuNulo(dadosFiscais.get("TPO_IPI").toString().equals("") ? null
									: dadosFiscais.get("TPO_IPI"));
							vlr_base_calculo_ipi = this
									.toArrayOuNulo(dadosFiscais.get("BC_IPI").toString().equals("") ? null
											: dadosFiscais.get("BC_IPI"));
							per_ipi = this.toArrayOuNulo(dadosFiscais.get("PER_IPI").toString().equals("") ? null
									: dadosFiscais.get("PER_IPI"));
							vlr_ipi = this.toArrayOuNulo(dadosFiscais.get("VLR_IPI").toString().equals("") ? null
									: dadosFiscais.get("VLR_IPI"));
							vlr_ipi_outros = this
									.toArrayOuNulo(dadosFiscais.get("OUTROS_IPI").toString().equals("") ? null
											: dadosFiscais.get("OUTROS_IPI"));
							vlr_ipi_isento = this
									.toArrayOuNulo(dadosFiscais.get("INSENTO_IPI").toString().equals("") ? null
											: dadosFiscais.get("INSENTO_IPI"));
						} else {
							continue;
						}
					}
				} else {
					qtd_devolvida_nf = this.toArrayOuNulo(null);
					vlr_unitario = this.toArrayOuNulo(null);
					per_desconto = this.toArrayOuNulo(null);
					vlr_total_item = this.toArrayOuNulo(null);
					vlr_desconto = this.toArrayOuNulo(null);
					cod_sit_tributaria = this.toArrayOuNulo(null);
					tpo_trib_icms = this.toArrayOuNulo(null);
					vlr_base_calculo_icms = this.toArrayOuNulo(null);
					per_icms = this.toArrayOuNulo(null);
					vlr_icms_subst = this.toArrayOuNulo(null);
					vlr_icms_outros = this.toArrayOuNulo(null);
					vlr_icms_isento = this.toArrayOuNulo(null);
					cod_sit_trib_ipi = this.toArrayOuNulo(null);
					tpo_trib_ipi = this.toArrayOuNulo(null);
					vlr_base_calculo_ipi = this.toArrayOuNulo(null);
					per_ipi = this.toArrayOuNulo(null);
					vlr_ipi = this.toArrayOuNulo(null);
					vlr_ipi_outros = this.toArrayOuNulo(null);
					vlr_ipi_isento = this.toArrayOuNulo(null);
					tpo_produto_sia = this.toArrayOuNulo(null);
					cod_produto_sia = this.toArrayOuNulo(null);
					tpo_trib_pis_cofins = this.toArrayOuNulo(null);
					cod_sit_trib_pis_cofins = this.toArrayOuNulo(null);
					per_pis = this.toArrayOuNulo(null);
					per_cofins = this.toArrayOuNulo(null);
					vlr_base_calculo_pis = this.toArrayOuNulo(null);
					vlr_base_calculo_cofins = this.toArrayOuNulo(null);
					vlr_pis = this.toArrayOuNulo(null);
					vlr_cofins = this.toArrayOuNulo(null);
					vlr_icms = this.toArrayOuNulo(null);
				}

			}

			oArray[0] = id_filial == null ? null : new Integer("" + id_filial[0]); // id_filial number,
			oArray[1] = num_devolucao == null ? null : new Integer("" + num_devolucao[0]); // num_devolucao number,
			aux = cabec.get("DTA_DEVOLUCAO").toString();
			if (aux.toString().equals("")) {
				oArray[2] = null;
			} else {
				sdf = new SimpleDateFormat("dd/MM/yyyy");
				sdf.parse("" + aux);
				sdf.getCalendar().getTime().getTime();
				oArray[2] = new java.sql.Timestamp(sdf.getCalendar().getTime().getTime());
			}
			oArray[3] = cod_tipo_devolucao == null ? null : new Integer("" + cod_tipo_devolucao[0].toString().trim()); // cod_tipo_devolucao
																														// number,
			oArray[4] = cod_produto == null ? null : new Integer("" + cod_produto[0].toString().trim()); // cod_produto
																											// number,
			oArray[5] = qtd_truncada_empresa == null ? null
					: new Integer("" + qtd_truncada_empresa[0].toString().trim()); // qtd_truncada_empresa number,
			oArray[6] = qtd_trucado_cliente == null ? null : new Integer("" + qtd_trucado_cliente[0].toString().trim()); // qtd_trucado_cliente
																															// number,
			oArray[7] = qtd_devolvida == null ? null : new Integer("" + qtd_devolvida[0].toString().trim()); // qtd_devolvida
																												// number,
			oArray[8] = qtd_recusada == null ? null : new Integer("" + qtd_recusada[0].toString().trim()); // qtd_recusada
																											// number,
			oArray[9] = num_credito == null ? null : new Integer("" + num_credito[0].toString().trim()); // num_credito
																											// number,
			oArray[10] = num_troca == null ? null : new Integer("" + num_troca[0].toString().trim()); // num_troca
																										// number,
			oArray[11] = desc_produto == null ? null : desc_produto[0].toString().trim(); // des_produto Varchar2(300),
			oArray[12] = num_isbn == null ? null : num_isbn[0].toString().trim(); // num_isbn Varchar2(13),
			oArray[13] = nom_empresa == null ? null : nom_empresa[0].toString().trim(); // nom_empresa_produto
																						// Varchar2(200),
			oArray[14] = id_empresa_grupo == null ? null : new Integer("" + id_empresa_grupo[0].toString().trim()); // id_empresa_grupo
																													// Number,
			oArray[15] = tpo_operacao == null ? null : tpo_operacao[0].toString().trim(); // tpo_operacao_bd
																							// Varchar2(1),
			oArray[16] = cod_local_nota == null ? null : new Integer("" + cod_local_nota[0].toString().trim()); // cod_local_nota
																												// Number,
			oArray[17] = num_sequencial_nota == null ? null
					: new Integer("" + num_sequencial_nota[0].toString().trim()); // num_sequencial_nota number(6),
			oArray[18] = tpo_nota_fiscal == null ? null : tpo_nota_fiscal[0].toString();// tpo_nota_fiscal char(2),
			oArray[19] = num_ano_nota_fiscal == null ? null
					: new Integer("" + num_ano_nota_fiscal[0].toString().trim()); // num_ano_nota_fiscal number(2),
			oArray[20] = qtd_item_nf == null ? null : new Float("" + qtd_item_nf[0].toString().trim()); // qtd_item_nf
																										// number(10,3),
			oArray[21] = qtd_devolvida_nf == null ? null : new Float("" + qtd_devolvida_nf[0].toString().trim()); // qtd_devolvida_nf
																													// number(10,3),
			oArray[22] = vlr_unitario == null ? null : new Float("" + vlr_unitario[0].toString().trim()); // vlr_unitario
																											// number(10,3),
			oArray[23] = per_desconto == null ? null : new Float("" + per_desconto[0].toString().trim()); // per_desconto
																											// number(5,2),
			oArray[24] = seq_item_consig == null ? null : new Integer("" + seq_item_consig[0].toString().trim()); // seq_item_consig
																													// number(9),
			oArray[25] = vlr_total_item == null ? null : new Float("" + vlr_total_item[0].toString().trim()); // vlr_total_item
																												// Number(13,2),
			oArray[26] = vlr_desconto == null ? null : new Float("" + vlr_desconto[0].toString().trim()); // vlr_desconto
																											// Number(13,2),
			oArray[27] = cod_sit_tributaria == null ? null : cod_sit_tributaria[0].toString().trim(); // cod_sit_tributaria
																										// varchar2(3),
			oArray[28] = tpo_trib_icms == null ? null : new Integer("" + tpo_trib_icms[0].toString().trim()); // tpo_trib_icms
																												// number(1),
			oArray[29] = vlr_base_calculo_icms == null ? null
					: new Float("" + vlr_base_calculo_icms[0].toString().trim()); // vlr_base_calculo_icms number(10,3),
			oArray[30] = per_icms == null ? null : new Float("" + per_icms[0].toString().trim()); // per_icms
																									// number(5,2),
			oArray[31] = vlr_icms_subst == null ? null : new Float("" + vlr_icms_subst[0].toString().trim()); // vlr_icms_subst
																												// number(13,2),
			oArray[32] = vlr_icms_outros == null ? null : new Float("" + vlr_icms_outros[0].toString().trim()); // vlr_icms_outros
																												// number(13,2),
			oArray[33] = vlr_icms_isento == null ? null : new Float("" + vlr_icms_isento[0].toString().trim()); // vlr_icms_isento
																												// number(13,2),
			oArray[34] = cod_sit_trib_ipi == null ? null : new Integer("" + cod_sit_trib_ipi[0].toString().trim()); // cod_sit_trib_ipi
																													// number(2),
			oArray[35] = tpo_trib_ipi == null ? null : new Integer("" + tpo_trib_ipi[0].toString().trim()); // tpo_trib_ipi
																											// number(1),
			oArray[36] = vlr_base_calculo_ipi == null ? null
					: new Float("" + vlr_base_calculo_ipi[0].toString().trim()); // vlr_base_calculo_ipi number(10,3),
			oArray[37] = per_ipi == null ? null : new Float("" + per_ipi[0].toString().trim()); // per_ipi number(5,2),
			oArray[38] = vlr_ipi == null ? null : new Float("" + vlr_ipi[0].toString().trim()); // vlr_ipi number(13,2),
			oArray[39] = vlr_ipi_outros == null ? null : new Float("" + vlr_ipi_outros[0].toString().trim()); // vlr_ipi_outros
																												// number(13,2),
			oArray[40] = vlr_ipi_isento == null ? null : new Float("" + vlr_ipi_isento[0].toString().trim()); // vlr_ipi_isento
																												// number(13,2),
			oArray[41] = tpo_produto_sia == null ? null : new Integer("" + tpo_produto_sia[0].toString().trim()); // tpo_produto_sia
																													// Number,
			oArray[42] = cod_produto_sia == null ? null : new Integer("" + cod_produto_sia[0].toString().trim()); // cod_produto_sia
																													// Number,
			oArray[43] = tpo_trib_pis_cofins == null ? null
					: new Integer("" + tpo_trib_pis_cofins[0].toString().trim()); // tpo_trib_pis_cofins number(1),
			oArray[44] = cod_sit_trib_pis_cofins == null ? null
					: new Integer("" + cod_sit_trib_pis_cofins[0].toString().trim()); // cod_sit_trib_pis_cofins
																						// number(2),
			oArray[45] = per_pis == null ? null : new Float("" + per_pis[0].toString().trim()); // per_pis number(5,2),
			oArray[46] = per_cofins == null ? null : new Float("" + per_cofins[0].toString().trim()); // per_cofins
																										// number(5,2),
			oArray[47] = vlr_base_calculo_pis == null ? null
					: new Float("" + vlr_base_calculo_pis[0].toString().trim()); // vlr_base_calculo_pis number(10,3),
			oArray[48] = vlr_base_calculo_cofins == null ? null
					: new Float("" + vlr_base_calculo_cofins[0].toString().trim()); // vlr_base_calculo_cofins
																					// number(10,3),
			oArray[49] = vlr_pis == null ? null : new Float("" + vlr_pis[0].toString().trim()); // vlr_pis number(13,2),
			oArray[50] = vlr_cofins == null ? null : new Float("" + vlr_cofins[0].toString().trim()); // vlr_cofins
																										// number(13,2),
			oArray[51] = vlr_icms == null ? null : new Float("" + vlr_icms[0].toString().trim()); // vlr_icms
																									// number(13,2),
			oArray[52] = qtd_cliente == null ? null : new Float("" + qtd_cliente[0].toString().trim()); // qtd_cliente
																										// number,
			oArray[53] = saldo_nota == null ? null : new Float("" + saldo_nota[0].toString().trim()); // saldo_nota
																										// number(13,2),
			System.out.println(cod_produto[0]);
			oArray[54] = qtd_faturado == null ? null : new Float("" + qtd_faturado[0].toString().trim()); // qtd_faturada
																											// number(13,2),

			if (lstItem == null)
				lstItem = new ArrayList<Struct>();

			lstItem.add(new STRUCT(structdescItem, flatConn, oArray));
			mItens.put("itens", lstItem);
		}
		return mItens;
	}