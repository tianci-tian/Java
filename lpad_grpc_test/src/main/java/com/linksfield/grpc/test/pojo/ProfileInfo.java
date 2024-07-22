package com.linksfield.grpc.test.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileInfo {
	public String m_status;
	public String m_iccid;
	public String m_serviceProviderName;
	public String m_profileName;
	
}
