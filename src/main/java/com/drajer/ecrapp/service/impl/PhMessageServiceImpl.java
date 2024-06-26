package com.drajer.ecrapp.service.impl;

import com.drajer.bsa.model.PublicHealthMessage;
import com.drajer.ecrapp.dao.PhMessageDao;
import com.drajer.ecrapp.service.PhMessageService;
import java.util.List;
import java.util.Map;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class PhMessageServiceImpl implements PhMessageService {

  @Autowired PhMessageDao phMessageDao;

  public List<PublicHealthMessage> getPhMessageData(Map<String, String> searchParams) {
    return phMessageDao.getPhMessageData(searchParams);
  }
}
